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
package org.n52.wacodis.javaps.algorithms.snap;

import java.util.HashMap;
import java.util.Map;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.io.GenericFileData;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.n52.wacodis.javaps.io.data.binding.complex.GeotiffFileDataBinding;
import org.n52.wacodis.javaps.io.data.binding.complex.ProductMetadataBinding;
import org.n52.wacodis.javaps.io.metadata.ProductMetadata;

/**
 *
 * @author LukasButzmann
 */
@Algorithm(
        identifier = "de.hsbo.wacodis.snap.wdvi",
        title = "Weighted Difference Vegetation Index",
        abstrakt = "Weighted Difference Vegetation Index retrieves the Isovegetation lines parallel to soil line. Soil line has an arbitrary slope and passes through origin.",
        version = "1.0.0",
        storeSupported = true,
        statusSupported = true)
public class WdviSnapAlgorithm extends AbstractSnapAlgorithm {

    private static final String PROCESS_ID = "de.hsbo.wacodis.snap.wdvi";
    private static final String WDVI_OPERATOR_NAME = "WdviOp";
    private static final String RESULT_NAME_PREFIX = "snap_wdvi_result";

    @LiteralInput(
            identifier = "SENTINEL_2_IMAGE_SOURCE",
            title = "Sentinel-2 image source",
            abstrakt = "Sources for the Sentinel-2 scene",
            minOccurs = 1,
            maxOccurs = 1)
    public void setOpticalImagesSources(String value) {
        this.setSentinel2ImageSource(value);
    }

    @ComplexOutput(
            identifier = "PRODUCT",
            binding = GeotiffFileDataBinding.class
    )
    public GenericFileData getOutput() throws WacodisProcessingException {
        return this.createProductOutput(this.getResultPath());
    }

    @ComplexOutput(
            identifier = "METADATA",
            binding = ProductMetadataBinding.class
    )
    public ProductMetadata getMetadata() {
        return this.getProductMetadata();
    }

    @Execute
    public void callExecute() throws WacodisProcessingException {
        this.execute();
    }

    public String getProcessId() {
        return PROCESS_ID;
    }

    public String getResultNamePrefix() {
        return RESULT_NAME_PREFIX;
    }

    public String getOperatorName() {
        return WDVI_OPERATOR_NAME;
    }

    public Map<String, Object> prepareOperationParameters() {
        Map<String, Object> parameters = new HashMap();
        parameters.put("redFactor", 1.0f);
        parameters.put("nirFactor", 1.0f);
        parameters.put("slopeSoilLine", 1.5f);
        parameters.put("redSourceBand", "B4");
        parameters.put("nirSourceBand", "B8");
        return parameters;
    }

}
