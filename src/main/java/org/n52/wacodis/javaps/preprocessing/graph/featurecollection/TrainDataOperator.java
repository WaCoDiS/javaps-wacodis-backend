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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.n52.wacodis.javaps.preprocessing.graph.InputDataOperator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Adds column "class" to a {@link SimpleFeatureCollection} with TrainData 
 * for LandCoverClassification, if it does not already exist
 * 
 * @author LukasButzmann
 */
public class TrainDataOperator extends InputDataOperator<SimpleFeatureCollection> {

    private String category;
    private String attributeName;

    public TrainDataOperator(String attributeName) {
        this.attributeName = attributeName;
        this.category = "class";
    }
    
    public TrainDataOperator(String attributeName, String category) {
        this.attributeName = attributeName;
        this.category = category;
    }

    @Override
    public String getName() {
        return "org.wacodis.writer.TrainDataOperator";
    }

    @Override
    public SimpleFeatureCollection process(SimpleFeatureCollection input) throws WacodisProcessingException {

        
        if (hasDescriptor(attributeName, input)) {
            
            if (attributeName != category){
                //NEW SCHEMA
                SimpleFeatureType schema = input.getSchema();
                // create new schema
                SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
                builder.setName(schema.getName());
                builder.setSuperType((SimpleFeatureType) schema.getSuper());
                builder.addAll(schema.getAttributeDescriptors());
                // add new attribute
                builder.add(category, String.class);
                // build new schema
                SimpleFeatureType nSchema = builder.buildFeatureType();

                // loop through features adding new attribute
                List<SimpleFeature> features = new ArrayList<>();
                SimpleFeatureIterator iterator = input.features();
                Map<Object, Integer> attributeMap = new HashMap<>();

                try {
                  int i = 1;
                  while (iterator.hasNext()) {

                    SimpleFeature f = iterator.next();
                    Object key = f.getAttribute(attributeName);


                    if (!attributeMap.containsKey(key)) {
                        attributeMap.put(key, i++);
                    }

                    //copy feature
                    //attributeMap.put(key, attributeMap.get(key));

                    SimpleFeature f2 = DataUtilities.reType(nSchema, f);
                    f2.setAttribute(category, attributeMap.get(key));
                    features.add(f2);
                  }
                } finally {
                    iterator.close();
                }

                SimpleFeatureCollection simpleCollection = DataUtilities.collection(features);
                return simpleCollection;
            }else{
                return(input);
            }
        }else{
            throw new WacodisProcessingException("The Features in InputCollection don't have the Attribute <"+attributeName+">!");
        }
    }

    
    public boolean hasDescriptor(String descriptorName, SimpleFeatureCollection input){
        for(AttributeDescriptor descriptor : input.getSchema().getAttributeDescriptors()){
            if (descriptor.getLocalName().equalsIgnoreCase(attributeName)){
                return true;
            }
        }       
        return false;
    }
    
    @Override
    public String getSupportedClassName() {
        return SimpleFeatureCollection.class.getName();
    }

}
