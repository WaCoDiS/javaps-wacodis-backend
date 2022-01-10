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
package org.n52.wacodis.javaps.io.data.binding.complex;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.javaps.io.complex.ComplexData;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class FeatureCollectionBinding implements ComplexData<SimpleFeatureCollection> {

    private static final long serialVersionUID = 1626429242623057672L;

    protected transient SimpleFeatureCollection simpleFeatureCollection;

    public FeatureCollectionBinding(SimpleFeatureCollection payload) {
        this.simpleFeatureCollection = payload;
    }

    @Override
    public SimpleFeatureCollection getPayload() {
        return simpleFeatureCollection;
    }

    @Override
    public Class<?> getSupportedClass() {
        return SimpleFeatureCollection.class;
    }

}
