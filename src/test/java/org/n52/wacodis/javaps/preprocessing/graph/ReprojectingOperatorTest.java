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
package org.n52.wacodis.javaps.preprocessing.graph;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.n52.wacodis.javaps.preprocessing.graph.featurecollection.ReprojectingOperator;
import org.opengis.referencing.FactoryException;
/**
 *
 * @author LukasButzmann
 */
public class ReprojectingOperatorTest {
    
    private static final String REFERENCE_DATA_FILE_NAME = "test-reference-data.json";
    private static final String TARGET_EPSG_CODE = "EPSG:4326";

    private SimpleFeatureCollection featureCollection;
    private ReprojectingOperator operator;
    
    @Before
    public void init() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(REFERENCE_DATA_FILE_NAME);
        this.featureCollection = (SimpleFeatureCollection) new FeatureJSON().readFeatureCollection(input);
        this.operator = new ReprojectingOperator(TARGET_EPSG_CODE);
    }

    @Test
    public void testPreprocessingForValidFeatureCollection() throws WacodisProcessingException, MalformedURLException, IOException, FactoryException {
        SimpleFeatureCollection resultCollection = this.operator.process(this.featureCollection);

        Assert.assertFalse(resultCollection.isEmpty());
        Assert.assertEquals(CRS.decode(TARGET_EPSG_CODE).toWKT(),
                resultCollection.getSchema().getCoordinateReferenceSystem().toWKT());
    }
}
