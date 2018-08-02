/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.wacodis.javaps.io.datahandler.parser;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.geotools.GML;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.DecodingException;
import org.n52.javaps.io.InputHandler;
import org.n52.shetland.ogc.wps.Format;
import org.n52.wacodis.javaps.io.data.binding.complex.FeatureCollectionBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Properties(defaultPropertyFileName = "gml.properties")
public class Gml3Parser extends AbstractPropertiesInputOutputHandler implements InputHandler {

    private static Logger LOG = LoggerFactory.getLogger(Gml3Parser.class);

    public Gml3Parser() {
        super();
        // Setting the system-wide default at startup time
        System.setProperty("org.geotools.referencing.forceXY", "true");
        addSupportedBinding(FeatureCollectionBinding.class);
    }

    @Override
    public FeatureCollectionBinding parse(TypedProcessInputDescription<?> description, InputStream input, Format format) throws IOException, DecodingException {
        GML gml = new GML(GML.Version.GML3);
        try {
            SimpleFeatureCollection fc = gml.decodeFeatureCollection(input);
            return new FeatureCollectionBinding(fc);
        } catch (SAXException ex) {
            LOG.error("Error while parsing file", ex);
            throw new DecodingException("Could not parse file", ex);
        } catch (ParserConfigurationException ex) {
            LOG.error("Error caused by invalid parser configuration", ex);
            throw new DecodingException("Could not parse file", ex);
        }
    }
}
