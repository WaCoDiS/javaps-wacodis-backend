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
package org.n52.wacodis.javaps.preprocessing.graph.filedata;

import org.n52.javaps.io.GenericFileData;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
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
            return new File(fileName);
        } else {
            throw new WacodisProcessingException("Error while writing file.");
        }
    }

    @Override
    public String getSupportedClassName() {
        return GenericFileData.class.getName();
    }
}
