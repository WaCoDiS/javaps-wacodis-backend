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
package org.n52.wacodis.javaps.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.n52.wacodis.javaps.utils.LoggerProgressMonitor;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocessor for converting a Sentinel-2 image scene from SAFE format to
 * multiband GeoTIFFs. For each band group that share the same spatial
 * resolution a seperate multiband GeoTIFF will be created.
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class Sentinel2Preprocessor implements InputDataPreprocessor<Product> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Sentinel2Preprocessor.class);

    private static final String TIFF_FILE_EXTENSION = ".tif";

    private static final String GEOTIFF_TYPE = "GeoTIFF";

    private final String outputFilenamesSuffix;

    private boolean useAllBands;

    private static final String[] SUPPORTED_PRODUCT_TYPES = new String[]{
        "S2_MSI_Level-1C", "S2_MSI_Level-2A"
    };

    /**
     * Constructs a new default Sentinel-2 preprocessor that only considers the
     * bands for the highest spatial resolution
     *
     */
    public Sentinel2Preprocessor() {
        this.outputFilenamesSuffix = "";
    }

    /**
     * Constructs a new Sentinel-2 preprocessor
     *
     * @param useAllBands indicates whether to consider all spectral bands for
     * every spatial resolution or to use only the bands for the highest spatial
     * resolution
     */
    public Sentinel2Preprocessor(boolean useAllBands) {
        this(useAllBands, "");
    }

    public Sentinel2Preprocessor(boolean useAllBands, String outputFilenamesSuffix) {
        this.outputFilenamesSuffix = outputFilenamesSuffix;
        this.useAllBands = useAllBands;
    }

    @Override
    public List<File> preprocess(Product product, String outputDirectoryPath) throws WacodisProcessingException {
//        Product inProduct = ProductIO.readProduct(inputFilePath);
        if (!isProductTypeSupported(product.getProductType())) {
            throw new WacodisProcessingException("Product type is not supported.");
        };
        Band[] bands = product.getBands();
        String productName = product.getName();

        Map<Integer, Set<Band>> bandMap = groupSpectralRasterBands(bands);
        try {
            if (useAllBands) {
                return createGeotiffForAllBandResolutions(bandMap, product, productName, outputDirectoryPath);
            } else {
                return createGeotiffForHighestBandResolution(bandMap, product, productName, outputDirectoryPath);
            }
        } catch (IOException ex) {
            throw new WacodisProcessingException("Error while creating GeoTiff", ex);
        }
    }

    private Map<Integer, Set<Band>> groupSpectralRasterBands(Band[] bands) {
        Map<Integer, Set<Band>> bandMap = new HashMap();
        int numberBands = bands.length;

        for (int i = 0; i < numberBands; i++) {
            if (bands[i].getSpectralBandIndex() != -1) {
                if (bands[i].getRasterWidth() != bands[i].getRasterHeight()) {
                    continue;
                } else {
                    if (bandMap.containsKey(bands[i].getRasterWidth())) {
                        bandMap.get(bands[i].getRasterWidth()).add(bands[i]);
                    } else {
                        Set bandSet = new HashSet();
                        bandSet.add(bands[i]);
                        bandMap.put(bands[i].getRasterWidth(), bandSet);
                    }
                }
            }
        }
        return bandMap;
    }

    private List<File> createGeotiffForAllBandResolutions(Map<Integer, Set<Band>> bandMap, Product inProduct, String productName, String outputDirectoryPath) throws IOException {
        List<File> fileList = new ArrayList();
        for (int key : bandMap.keySet()) {
            fileList.add(
                    createMultispectralGeotiff(
                            bandMap.get(key), inProduct,
                            productName + "_" + String.valueOf(key),
                            outputDirectoryPath)
            );
        }
        return fileList;
    }

    private List<File> createGeotiffForHighestBandResolution(Map<Integer, Set<Band>> bandMap, Product inProduct, String productName, String outputDirectoryPath) throws IOException {
        List<File> fileList = new ArrayList();
        Set<Band> highResBands = bandMap.get(Collections.max(bandMap.keySet()));

        fileList.add(createMultispectralGeotiff(highResBands, inProduct, productName, outputDirectoryPath));
        return fileList;
    }

    private File createMultispectralGeotiff(Set<Band> bands, Product inProduct, String productName, String outputDirectoryPath) throws IOException {
        Product outProduct = new Product(productName, GEOTIFF_TYPE);

        bands.forEach(b -> {
            ProductUtils.copyBand(b.getName(), inProduct, outProduct, true);
        });

        ProductUtils.copyGeoCoding(inProduct, outProduct);
        ProductUtils.copyMetadata(inProduct, outProduct);

        LoggerProgressMonitor monitor = new LoggerProgressMonitor(LOGGER);

        String outFilePath = FilenameUtils.concat(outputDirectoryPath, productName + this.outputFilenamesSuffix + TIFF_FILE_EXTENSION);

        ProductIO.writeProduct(
                outProduct,
                outFilePath,
                GEOTIFF_TYPE,
                monitor
        );
        return new File(outFilePath);
    }

    private boolean isProductTypeSupported(String productType) {
        return Arrays.stream(SUPPORTED_PRODUCT_TYPES)
                .anyMatch(t -> t.equals(productType));
    }

}
