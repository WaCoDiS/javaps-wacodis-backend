/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.wacodis.javaps.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexInput;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.gt.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.io.GenericFileData;
import org.n52.wacodis.javaps.exceptions.GeometryParseException;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.n52.wacodis.javaps.command.AbstractCommandValue;
import org.n52.wacodis.javaps.io.data.binding.complex.GeotiffFileDataBinding;
import org.n52.wacodis.javaps.io.data.binding.complex.ProductMetadataBinding;
import org.n52.wacodis.javaps.io.http.SentinelFileDownloader;
import org.n52.wacodis.javaps.io.metadata.ProductMetadata;
import org.n52.wacodis.javaps.preprocessing.GptPreprocessor;
import org.n52.wacodis.javaps.preprocessing.InputDataPreprocessor;
import org.n52.wacodis.javaps.preprocessing.graph.InputDataOperator;
import org.n52.wacodis.javaps.preprocessing.graph.InputDataWriter;
import org.n52.wacodis.javaps.preprocessing.graph.PreprocessingExecutor;
import org.n52.wacodis.javaps.preprocessing.graph.featurecollection.ReprojectingOperator;
import org.n52.wacodis.javaps.preprocessing.graph.featurecollection.ShapeWriter;
import org.n52.wacodis.javaps.preprocessing.graph.featurecollection.TrainDataOperator;
import org.n52.wacodis.javaps.utils.GeometryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Algorithm(
        identifier = "de.hsbo.wacodis.land_cover_classification",
        title = "Land Cover Classification",
        abstrakt = "Perform a land cover classification for optical images.",
        version = "1.0.0",
        storeSupported = true,
        statusSupported = true)
public class LandCoverClassificationAlgorithm extends AbstractAlgorithm {

    private static final String PROCESS_ID = "de.hsbo.wacodis.land_cover_classification";
    private static final String TIFF_EXTENSION = ".tif";
    private static final String RESULTNAMEPREFIX = "land_cover_classification_result";
    private static final String TOOL_CONFIG = "land-cover-classification.yml";
    private static final String GPF_FILE = "S2_GeoTIFF_Composition.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(LandCoverClassificationAlgorithm.class);

    @Autowired
    private SentinelFileDownloader sentinelDownloader;

    private List<String> opticalImagesSources;
    private String areaOfInterest;
    private SimpleFeatureCollection referenceData;
    private ProductMetadata productMetadata;
    private List<Product> sentinelProductList;

    @LiteralInput(
            identifier = "OPTICAL_IMAGES_SOURCES",
            title = "Optical images sources",
            abstrakt = "Sources for the optical images",
            minOccurs = 1,
            maxOccurs = 1)
    public void setOpticalImagesSources(List<String> value) {
        this.opticalImagesSources = value;
    }

    @LiteralInput(
            identifier = "AREA_OF_INTEREST",
            title = "Area of interest",
            abstrakt = "Area of interest of the optical image in GeoJSON-Format e.g. [7.1234, 52.1234, 7.9876, 52.9876]. [0,0,0,0] uses the entire area of the image.",
            minOccurs = 1,
            maxOccurs = 1)
    public void setAreaOfInterst(String value) {
        this.areaOfInterest = value;
    }

    @ComplexInput(
            identifier = "REFERENCE_DATA",
            title = "Reference data",
            abstrakt = "Reference data for land cover classification",
            minOccurs = 1,
            maxOccurs = 1,
            binding = GTVectorDataBinding.class
    )
    public void setReferenceData(SimpleFeatureCollection value) {
        this.referenceData = value;
    }

    @ComplexOutput(
            identifier = "PRODUCT",
            binding = GeotiffFileDataBinding.class
    )
    public GenericFileData getOutput() throws WacodisProcessingException {
        return this.createProductOutput(this.getResultFile());
    }

    @ComplexOutput(
            identifier = "METADATA",
            binding = ProductMetadataBinding.class
    )
    public ProductMetadata getMetadata() {
        return this.productMetadata;
    }

    @Execute
    public void execute() throws WacodisProcessingException {
        this.executeProcess();
        this.productMetadata = this.createProductMetadata(this.sentinelProductList);
    }

    @Override
    public String getProcessId(){
        return PROCESS_ID;
    }

    @Override
    public String getToolConfigName() {
        return TOOL_CONFIG;
    }

    @Override
    public String getResultNamePrefix() {
        return RESULTNAMEPREFIX;
    }

    @Override
    public String getGpfConfigName() {
        return GPF_FILE;
    }

    @Override
    public Map<String, AbstractCommandValue> createInputArgumentValues(String basePath) throws WacodisProcessingException {
        Map<String, AbstractCommandValue> inputArgumentValues = new HashMap();

        inputArgumentValues.put("OPTICAL_IMAGES_SOURCES", this.createInputValue(basePath, this.preprocessOpticalImages(), true));
        inputArgumentValues.put("REFERENCE_DATA", this.createInputValue(basePath, this.preprocessReferenceData(), true));
        inputArgumentValues.put("RESULT_PATH", this.getResultPath(basePath));

        return inputArgumentValues;
    }

    private List<File> preprocessOpticalImages() throws WacodisProcessingException {
        HashMap<String, String> parameters = new HashMap();
        try {
            parameters.put("area", GeometryUtils.geoJsonBboxToWkt(areaOfInterest));
        } catch (GeometryParseException ex) {
            throw new WacodisProcessingException("Error while trying to convert area of interest to WKT", ex);
        }
        InputDataPreprocessor imagePreprocessor = new GptPreprocessor(FilenameUtils.concat(this.getBackendConfig().getGpfDir(), GPF_FILE), parameters, TIFF_EXTENSION, this.getNamingSuffix());

        this.sentinelProductList = new ArrayList();
        List<File> preprocessedImages = new ArrayList();
        this.opticalImagesSources.forEach(ois -> {
            try {
                // Download satellite data
                File sentinelFile = sentinelDownloader.downloadSentinelFile(
                        ois,
                        this.getBackendConfig().getWorkingDirectory(),
                        false);
                Product sentinelProduct = ProductIO.readProduct(sentinelFile.getPath());
                this.sentinelProductList.add(sentinelProduct);
                List<File> preprocessedFiles = imagePreprocessor.preprocess(sentinelProduct, this.getBackendConfig().getWorkingDirectory());
                preprocessedFiles.forEach(pF -> {
                    try {
                        preprocessedImages.add(executeGdalWarp(pF, this.getBackendConfig().getEpsg()));
                    } catch (WacodisProcessingException ex) {
                        String message = String.format("Error while executing GDAL warp for file: %s", pF.getName());
                        LOGGER.error(message);
                        LOGGER.debug(message, ex);
                    }
                });
            } catch (IOException ex) {
                LOGGER.error("Error while retrieving Sentinel file: {}. Cause: {}", ois, ex.getMessage());
                LOGGER.debug("Error while retrieving Sentinel file: {}.", ois, ex);
            } catch (WacodisProcessingException ex) {
                LOGGER.error("Error while preprocessing Sentinel file: {}. Cause: {}", ois, ex);
                LOGGER.debug("Error while preprocessing Sentinel file: {}.", ois, ex);
            }
        });
        if (preprocessedImages.isEmpty()) {
            throw new WacodisProcessingException("No preprocessed Sentinel files available.");
        }
        return preprocessedImages;
    }

    private File preprocessReferenceData() throws WacodisProcessingException {

        String fileIdentifier = (this.getNamingSuffix() != null) ? this.getNamingSuffix() : UUID.randomUUID().toString();
        InputDataWriter shapeWriter = new ShapeWriter(new File(this.getBackendConfig().getWorkingDirectory(), "wacodis_traindata_" + fileIdentifier + ".shp"));

        InputDataOperator reprojectingOperator = new ReprojectingOperator(this.getBackendConfig().getEpsg());
        InputDataOperator trainDataOperator = new TrainDataOperator("class");
        List<InputDataOperator> referenceDataOperatorList = new ArrayList<>();
        referenceDataOperatorList.add(reprojectingOperator);
        referenceDataOperatorList.add(trainDataOperator);

        PreprocessingExecutor referencePreprocessor = new PreprocessingExecutor(shapeWriter, referenceDataOperatorList);
        File preprocessedReferenceData = referencePreprocessor.executeOperators(this.referenceData);

        return preprocessedReferenceData;
    }

}
