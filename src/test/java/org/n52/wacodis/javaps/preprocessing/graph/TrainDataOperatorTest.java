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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.n52.wacodis.javaps.preprocessing.graph.featurecollection.TrainDataOperator;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author Lukas Butzmann
 */
public class TrainDataOperatorTest {
    
    private static final String REFERENCE_DATA_FILE_NAME = "test-reference-data.json";
    private static final String REFERENCE_DATA_FILE_NAME_WITHOUT_CLASS_ATTRIBUTE = "test-reference-data-without-class.json";

    private SimpleFeatureCollection featureCollectionWithClassAttribute;
    private SimpleFeatureCollection featureCollectionWithoutClassAttribute;
    private TrainDataOperator operatorWithClass;
    private TrainDataOperator operatorWithoutClass;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void init() throws IOException {
        InputStream inputWithClass = this.getClass().getClassLoader().getResourceAsStream(REFERENCE_DATA_FILE_NAME);
        this.featureCollectionWithClassAttribute = (SimpleFeatureCollection) new FeatureJSON().readFeatureCollection(inputWithClass);
        this.operatorWithClass = new TrainDataOperator("class");
        
        InputStream inputWithoutClass = this.getClass().getClassLoader().getResourceAsStream(REFERENCE_DATA_FILE_NAME_WITHOUT_CLASS_ATTRIBUTE);
        this.featureCollectionWithoutClassAttribute = (SimpleFeatureCollection) new FeatureJSON().readFeatureCollection(inputWithoutClass);
        this.operatorWithoutClass = new TrainDataOperator("noclass");
    }

    @Test
    public void testPreprocessingForValidFeatureCollectionWithClass() throws WacodisProcessingException, MalformedURLException, IOException, FactoryException {
        SimpleFeatureCollection resultCollection = this.operatorWithClass.process(this.featureCollectionWithClassAttribute);
        Assert.assertFalse(resultCollection.isEmpty());
    }
    
    @Test
    public void testPreprocessingForValidFeatureCollectionWithoutClass() throws WacodisProcessingException, MalformedURLException, IOException, FactoryException {
        SimpleFeatureCollection resultCollection2 = this.operatorWithoutClass.process(this.featureCollectionWithoutClassAttribute);
        Assert.assertEquals("class", resultCollection2.getSchema().getDescriptor("class").getLocalName());
        Assert.assertFalse(resultCollection2.isEmpty());
    }
    
    @Test
    public void testPreprocessingThrowsException() throws WacodisProcessingException, MalformedURLException, IOException, FactoryException {
        thrown.expect(WacodisProcessingException.class);
        thrown.expectMessage("The Features in InputCollection don't have the Attribute <noclass>!");
        this.operatorWithoutClass.process(this.featureCollectionWithClassAttribute);
    } 
}
