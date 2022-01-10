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
package org.n52.wacodis.javaps.io.datahandler.generator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.EncodingException;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;
import org.n52.wacodis.javaps.io.metadata.ProductMetadata;
import org.n52.wacodis.javaps.io.data.binding.complex.ProductMetadataBinding;

/**
 * Generates product metadata in JSON format
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Properties(
        defaultPropertyFileName = "product-metadata.default.json",
        propertyFileName = "product-metadata.json")
public class ProductMetadataJsonGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {

    private ObjectMapper objectMapper;

    public ProductMetadataJsonGenerator() {
        super();
        addSupportedBinding(ProductMetadataBinding.class);
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JSR310Module());
    }

    @Override
    public InputStream generate(TypedProcessOutputDescription<?> description, Data<?> data, Format format) throws IOException, EncodingException {
        ProductMetadata metadata = ((ProductMetadataBinding) data).getPayload();
        
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(metadata));
    }

}
