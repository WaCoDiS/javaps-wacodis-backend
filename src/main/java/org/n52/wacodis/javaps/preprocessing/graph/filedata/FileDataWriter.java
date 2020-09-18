package org.n52.wacodis.javaps.preprocessing.graph.filedata;

import org.n52.javaps.io.GenericFileData;
import org.n52.wacodis.javaps.WacodisProcessingException;
import org.n52.wacodis.javaps.preprocessing.graph.InputDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class FileDataWriter extends InputDataWriter<GenericFileData> {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataWriter.class);

    public FileDataWriter(File targetFile) {
        super(targetFile);
    }

    @Override
    public String getWriterName() {
        return "org.wacodis.writer.FileDataWriter";
    }

    @Override
    public File write(GenericFileData input) throws WacodisProcessingException {
        String fileName = input.writeData(this.getTargetFile());
        if (fileName != null) {
            LOG.info("Writing file {} was successfull.", fileName);
            return this.getTargetFile();
        } else {
            throw new WacodisProcessingException("Error while writing file.");
        }
    }

    @Override
    public String getSupportedClassName() {
        return GenericFileData.class.getName();
    }
}
