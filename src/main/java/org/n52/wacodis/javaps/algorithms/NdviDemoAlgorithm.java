/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.wacodis.javaps.algorithms;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.io.GenericFileData;
import org.n52.wacodis.javaps.WacodisProcessingException;
import org.n52.wacodis.javaps.command.AbstractCommandValue;
import org.n52.wacodis.javaps.io.data.binding.complex.GeotiffFileDataBinding;
import org.n52.wacodis.javaps.io.data.binding.complex.ProductMetadataBinding;
import org.n52.wacodis.javaps.io.http.SentinelFileDownloader;
import org.n52.wacodis.javaps.io.metadata.ProductMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Algorithm(
        identifier = "de.hsbo.wacodis.ndvi_code_de",
        title = "Vegetation Density Normalized Difference Vegetation Index",
        abstrakt = "Perform a Vegetation Density Normalized Difference Vegetation Index calculation.",
        version = "1.0.0",
        storeSupported = true,
        statusSupported = true)
public class NdviDemoAlgorithm extends AbstractAlgorithm {

    private static final String PROCESS_ID = "de.hsbo.wacodis.ndvi_code_de";
    private static final String RESULTNAMEPREFIX = "ndvi_result";
    private static final String TOOL_CONFIG = "ndvi-code-de.yml";
    private static final String TIFF_EXTENSION = ".tif";

    private static final Logger LOGGER = LoggerFactory.getLogger(NdviDemoAlgorithm.class);

    @Autowired
    private SentinelFileDownloader sentinelDownloader;

    private String opticalImagesSource;
    private ProductMetadata productMetadata;
    private Product sentinelProduct;

    @LiteralInput(
            identifier = "OPTICAL_IMAGES_SOURCES",
            title = "Optical images sources",
            abstrakt = "Sources for the optical images",
            minOccurs = 1,
            maxOccurs = 1)
    public void setOpticalImagesSources(String value) {
        this.opticalImagesSource = value;
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
        this.productMetadata = this.createProductMetadata(Collections.singletonList(this.sentinelProduct));
    }

    @Override
    public String getProcessId() {
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
        return null;
    }

    @Override
    public File getResultFile() {
        return new File(this.getBackendConfig().getWorkingDirectory() + "/NDVI", this.getProductName());
    }

    @Override
    public Map<String, AbstractCommandValue> createInputArgumentValues(String basePath) throws WacodisProcessingException {
        Map<String, AbstractCommandValue> inputArgumentValues = new HashMap();

        File sentinelFile = this.preprocessOpticalImages();
        String path = sentinelFile.getPath();
        inputArgumentValues.put("RAW_OPTICAL_IMAGES_SOURCES", this.createInputValue("", sentinelFile, true));
        setProductName(String.join("",
                FilenameUtils.getBaseName(sentinelFile.getName()),
                "_NDVI_10m",
                TIFF_EXTENSION)
        );
//        inputArgumentValues.put("RESULT_PATH", this.getResultPath("/output/"));

        return inputArgumentValues;
    }

    private File preprocessOpticalImages() throws WacodisProcessingException {
        try {
            File sentinelFile = sentinelDownloader.downloadSentinelFile(
                    this.opticalImagesSource,
                    this.getBackendConfig().getWorkingDirectory());
            this.sentinelProduct = ProductIO.readProduct(sentinelFile.getPath());

            return sentinelFile;

        } catch (IOException ex) {
            LOGGER.debug("Error while reading Sentinel file: {}", this.opticalImagesSource, ex);
            throw new WacodisProcessingException("Could not preprocess Sentinel product", ex);
        }
    }

}
