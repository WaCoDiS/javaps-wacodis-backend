package org.n52.wacodis.javaps.io.datahandler.parser;

import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.javaps.io.*;
import org.n52.javaps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.shetland.ogc.wps.Format;
import org.n52.wacodis.javaps.io.data.binding.complex.GeotiffFileDataBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Properties(
        defaultPropertyFileName = "geotiff.default.json",
        propertyFileName = "geotiff.json")
public class GeotiffParser extends AbstractPropertiesInputOutputHandler implements InputHandler {

    private static Logger LOG = LoggerFactory.getLogger(GeotiffParser.class);

    public GeotiffParser() {
        super();
        addSupportedBinding(GeotiffFileDataBinding.class);
    }

    @Override
    public GeotiffFileDataBinding parse(TypedProcessInputDescription<?> description, InputStream input, Format format) throws IOException, DecodingException {
        Optional<String> mimeType = format.getMimeType();

        GenericFileData theData = new GenericFileData(input, mimeType.get());
        LOG.info("Found File Input " + mimeType);

        return new GeotiffFileDataBinding(theData);
    }
}
