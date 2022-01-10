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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.geotools.GML;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class ReferenceDataPreprocessorIT {

    private static final String REFERENCE_DATA_FILE_NAME = "test-reference-data.xml";
    private static final String TARGET_EPSG_CODE = "EPSG:4326";
    private static final String TMP_SHAPE_DIR_PREFIX = "tmp-ref-dir";

    private SimpleFeatureCollection featureCollection;
    private ReferenceDataPreprocessor preprocessor;
    private Path tmpShapeDir;

    @Before
    public void init() throws IOException, ParserConfigurationException, SAXException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(REFERENCE_DATA_FILE_NAME);
        GML gml = new GML(GML.Version.WFS1_1);
        this.featureCollection = gml.decodeFeatureCollection(input);
        this.preprocessor = new ReferenceDataPreprocessor(TARGET_EPSG_CODE);

        this.tmpShapeDir = Files.createTempDirectory(TMP_SHAPE_DIR_PREFIX);
    }

    @Test
    public void testPreprocessingForValidFeatureCollection() throws WacodisProcessingException, MalformedURLException, IOException, FactoryException {
        File result = this.preprocessor.preprocess(this.featureCollection,
                tmpShapeDir.toString()).get(0);

        SimpleFeatureCollection resultCollection = this.shpToFeatureCollection(result);

        Assert.assertFalse(resultCollection.isEmpty());
        Assert.assertEquals("the_geom", resultCollection.getSchema().getGeometryDescriptor().getLocalName());
        Assert.assertEquals(CRS.decode(TARGET_EPSG_CODE).toWKT(),
                resultCollection.getSchema().getCoordinateReferenceSystem().toWKT());
    }

    private SimpleFeatureCollection shpToFeatureCollection(File result) throws MalformedURLException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("url", result.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source
                = dataStore.getFeatureSource(typeName);
        SimpleFeatureCollection collection = (SimpleFeatureCollection) source.getFeatures();

        return collection;
    }

    @After
    public void shutdown() throws IOException {
        FileUtils.deleteDirectory(this.tmpShapeDir.toFile());
    }

}
