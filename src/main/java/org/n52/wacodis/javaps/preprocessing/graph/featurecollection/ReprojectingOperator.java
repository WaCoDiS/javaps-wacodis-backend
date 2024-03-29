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
package org.n52.wacodis.javaps.preprocessing.graph.featurecollection;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.n52.wacodis.javaps.exceptions.GeometryParseException;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;

import static org.n52.wacodis.javaps.utils.GeometryUtils.decodeCRS;

import org.n52.wacodis.javaps.preprocessing.graph.InputDataOperator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reprojects a {@link SimpleFeatureCollection} into a target CRS specified by a
 * certain EPSG code
 *
 * @author LukasButzmann
 */
public class ReprojectingOperator extends InputDataOperator<SimpleFeatureCollection> {

    private static final Logger LOG = LoggerFactory.getLogger(ReprojectingOperator.class);

    //    private CoordinateReferenceSystem targetCrs;
    private String targetEpsg;

    public ReprojectingOperator(String targetEpsg) {
        this.targetEpsg = targetEpsg;
    }

    @Override
    public String getName() {
        return "org.wacodis.operator.ReprojectingOperator";
    }

    @Override
    public SimpleFeatureCollection process(SimpleFeatureCollection input) throws WacodisProcessingException {
        CoordinateReferenceSystem targetCrs = null;
        try {
            targetCrs = decodeCRS(targetEpsg);
        } catch (GeometryParseException ex) {
            LOG.error("Could not decode epsg code " + this.targetEpsg);
            throw new WacodisProcessingException("Error while decoding epsg code", ex);
        }

        SimpleFeatureCollection output;
        CoordinateReferenceSystem sourceCrs = input.getSchema().getCoordinateReferenceSystem();

        if (sourceCrs != null && !sourceCrs.equals(targetCrs)) {
            output = new ReprojectingFeatureCollection(input, sourceCrs, targetCrs);
        } else {
            output = new ReprojectingFeatureCollection(input, targetCrs);
        }
        return output;
    }

    @Override
    public String getSupportedClassName() {
        return SimpleFeatureCollection.class.getName();
    }

}
