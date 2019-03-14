/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.wacodis.javaps.preprocessing;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Arrays;
import java.util.UUID;
import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ReferenceDataPreprocessor implements InputDataPreprocessor<SimpleFeatureCollection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataPreprocessor.class);

    private static final String OUTPUT_FILENAME_PREFIX = "wacodis_traindata_";
    private static final String LANDCOVERCLASS_ATTRIBUTE = "class";
    private static final String[] SHAPEFILE_EXTENSIONS = new String[]{"shp", "shx", "dbf", "prj", "fix"};

    private String epsg;

    public ReferenceDataPreprocessor(String epsg) {
        this.epsg = epsg;
    }

    public ReferenceDataPreprocessor() {
    }

    public String getEpsg() {
        return epsg;
    }

    public void setEpsg(String epsg) {
        this.epsg = epsg;
    }

    /**
     *
     * @param inputCollection
     * @param outputDirectoryPath
     * @return
     * @throws IOException
     */
    @Override
    public List<File> preprocess(SimpleFeatureCollection inputCollection, String outputDirectoryPath) throws IOException {
        File[] outputFiles = generateOutputFileNames(outputDirectoryPath);
        File referenceDataShapefile = outputFiles[0]; //.shp
        URL fileURL = referenceDataShapefile.toURI().toURL();

        //get datastore factory for shapefiles
        FileDataStoreFactorySpi dataStoreFactory = FileDataStoreFinder.getDataStoreFactory(SHAPEFILE_EXTENSIONS[0]); //.shp

        //params for creating shapefile
        Map<String, Serializable> params = new HashMap<>();
        params.put("url", fileURL); //filename
        params.put("create spatial index", Boolean.TRUE);

        //check if params are suitable for creating shapefile
        boolean canProcess = dataStoreFactory.canProcess(params);
        if (!canProcess) {
            String msg = "cannot create datastore, params insufficient for datastore factory of class " + dataStoreFactory.getClass().getSimpleName();
            LOGGER.debug(msg + ", params: " + System.lineSeparator() + params.toString());
            throw new IOException(msg);
        }
        
        boolean isInputSchemaValid = validateInputSchema(inputCollection.getSchema());
        if(!isInputSchemaValid){
            String msg = "cannot write features, input schema is invalid";
            LOGGER.debug(msg);
            throw new IOException(msg);
        }


        //create new shapefile datastore with schema for trainig data
        DataStore dataStore = dataStoreFactory.createNewDataStore(params);
        CoordinateReferenceSystem crs = determineCRS();
        Class geometryBinding = getGeometryTypeFromSchema(inputCollection.getSchema());
        SimpleFeatureType outputSchema = createReferenceDataFeatureType(crs, geometryBinding); //traning data schema
        dataStore.createSchema(outputSchema);

        writeFeaturesToDataStore(dataStore, inputCollection, referenceDataShapefile); //write features to shapefile

        return Arrays.asList(outputFiles);
    }

    private void writeFeaturesToDataStore(DataStore dataStore, SimpleFeatureCollection inputCollection, File referenceDataFile) throws IOException {
        Transaction transaction = new DefaultTransaction("create");

        String typeName = dataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

            featureStore.setTransaction(transaction);
            try {
                LOGGER.debug("starting transaction for file " + referenceDataFile.getName());

                featureStore.addFeatures(inputCollection);
                transaction.commit();

                LOGGER.debug("successfully commited to file " + referenceDataFile.getName());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                LOGGER.debug("error while writing to file " + referenceDataFile.getName() + ", rollback transaction", e);
                transaction.rollback();
            } finally {
                LOGGER.debug("closing transaction for file " + referenceDataFile.getName());
                transaction.close();
            }
        } else {
            LOGGER.error("cannot write features, unexpected feature source type " + featureSource.getClass().getSimpleName());
        }
    }

    /**
     * schema must include attribute 'class' of type Integer or Long
     * @param inputSchema
     * @return 
     */
    private boolean validateInputSchema(SimpleFeatureType inputSchema) {
        AttributeDescriptor classAttribute =  inputSchema.getDescriptor(LANDCOVERCLASS_ATTRIBUTE);
        
        if(classAttribute == null ){ //check if class attribute exits
            LOGGER.warn("input schema does not contain mandatory attribute " + LANDCOVERCLASS_ATTRIBUTE);
            return false;
        }else{ //check datatype of class attribute
            AttributeType type = inputSchema.getType(LANDCOVERCLASS_ATTRIBUTE);
            Class binding = type.getBinding();
            
            if(!binding.equals(Integer.class) && !binding.equals(Long.class)){
                LOGGER.warn("attribute " + LANDCOVERCLASS_ATTRIBUTE + " is of type " + binding.getSimpleName() + ", expected Integer or Long");
                return false;
            }
        }

        return true;
    }
    
    private Class getGeometryTypeFromSchema(SimpleFeatureType schema){
        GeometryDescriptor geomAttribute = schema.getGeometryDescriptor();
        GeometryType geomType = geomAttribute.getType();
        Class geomBinding = geomType.getBinding();
        
        LOGGER.debug("get geometry attribute " + geomAttribute.getLocalName() + " of type " + geomBinding.getSimpleName());
        
        if(!geomBinding.equals(Polygon.class) && !geomBinding.equals(MultiPolygon.class)){
            LOGGER.warn("geometry attribute " + geomAttribute.getLocalName() + " is of datatype " + geomBinding.getSimpleName() +" , expected Polygon or MultiPolygon");
        }
        
        return geomBinding;
    }
    

    

    public SimpleFeatureType retrieveDefaultSchema() {
        CoordinateReferenceSystem crs = determineCRS();
        return createReferenceDataFeatureType(crs, null); //MultiPolygon
    }

    /**
     * schema for landcover classification training data
     *
     * @param crs
     * @return
     */
    private SimpleFeatureType createReferenceDataFeatureType(CoordinateReferenceSystem crs, Class geometryBinding) {
        if(geometryBinding == null){
            geometryBinding = MultiPolygon.class;
            LOGGER.warn("geometry binding not set, assume MultiPolygon");
        }
        
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("referencedata");
        builder.setCRS(crs);

        builder.add("the_geom", geometryBinding); //geometry attribute
        builder.add("id", Integer.class);
        builder.add(LANDCOVERCLASS_ATTRIBUTE, Integer.class); //landcover classification

        return builder.buildFeatureType();
    }
    

    private CoordinateReferenceSystem determineCRS() {
        CoordinateReferenceSystem crs;

        if (this.epsg != null) {
            crs = decodeCRS(this.epsg);
        } else {
            LOGGER.warn("epsg is not set, assume default crs WGS84");
            crs = DefaultGeographicCRS.WGS84;
        }

        return crs;
    }

    private CoordinateReferenceSystem decodeCRS(String epsg) {
        CoordinateReferenceSystem crs;

        try {
            crs = CRS.decode(epsg);
        } catch (FactoryException ex) {
            LOGGER.error("could not decode epsg code " + epsg + ", using default crs WGS84", ex);
            crs = DefaultGeographicCRS.WGS84;
        }

        return crs;
    }

    private File[] generateOutputFileNames(String outputDirectoryPath) {
        UUID fileIdentifier = UUID.randomUUID();
        File[] outputFiles = new File[SHAPEFILE_EXTENSIONS.length];
        String fileName;

        for (int i = 0; i < outputFiles.length; i++) {
            fileName = OUTPUT_FILENAME_PREFIX + fileIdentifier.toString() + "." + SHAPEFILE_EXTENSIONS[i];
            outputFiles[i] = getOutputFile(outputDirectoryPath, fileName);
        }

        return outputFiles;
    }

    private File getOutputFile(String outputDirectoryPath, String fileName) {
        File file1 = new File(outputDirectoryPath);
        File file2 = new File(file1, fileName);

        return file2;
    }
}
