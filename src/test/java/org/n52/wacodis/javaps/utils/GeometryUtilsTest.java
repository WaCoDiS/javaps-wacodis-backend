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
package org.n52.wacodis.javaps.utils;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.wacodis.javaps.exceptions.GeometryParseException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class GeometryUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDecodeCRSForValidEpsgCode() throws GeometryParseException {
        String validEpsg = "EPSG:32632";
        CoordinateReferenceSystem crs = GeometryUtils.decodeCRS(validEpsg);

        Assert.assertEquals("WGS 84 / UTM zone 32N", crs.getName().getCode());
    }

    @Test
    public void testDecodeCRSThrowsExceptionForInvalidEpsgCode() throws GeometryParseException {
        String validEpsg = "eps:12345";

        thrown.expect(GeometryParseException.class);
        CoordinateReferenceSystem crs = GeometryUtils.decodeCRS(validEpsg);
    }

    @Test
    public void testGeoJsonBboxToWktForValidBbox() throws GeometryParseException {
        String bBox = "[7.1234, 52.1234, 7.9876, 52.9876]";
        String wkt = GeometryUtils.geoJsonBboxToWkt(bBox);

        Assert.assertEquals("POLYGON ((7.1234 52.1234,7.1234 52.9876,7.9876 52.9876,7.9876 52.1234,7.1234 52.1234))", wkt);
    }

    @Test
    public void testGeoJsonBboxToWktThrowsExceptionForInvalidBbox() throws GeometryParseException {
        String bBox = "[7.1234, 52.1234, 7.9876]";

        thrown.expect(GeometryParseException.class);
        String wkt = GeometryUtils.geoJsonBboxToWkt(bBox);
    }
}
