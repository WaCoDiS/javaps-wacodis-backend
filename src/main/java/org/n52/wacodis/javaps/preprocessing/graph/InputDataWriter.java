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
package org.n52.wacodis.javaps.preprocessing.graph;

import java.io.File;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;

/**
 * Interface for writing an input in a certain file format to a target
 * directory.
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public abstract class InputDataWriter<T> {

    private final File targetFile;

    /**
     * Instantiates a new writer for input data that stores the data to the
     * specified target directory.
     *
     * @param targetFile The File to which the input data will be
     * written.
     */
    public InputDataWriter(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Get the unique name for this writer.
     *
     * @return The writer's unique name
     */
    public abstract String getWriterName();

    /**
     * Writes the input to a specified target directory.
     *
     * @param input The input to write out.
     * @return {@link File} that contains the preprocessed input data.
     * @throws WacodisProcessingException
     */
    public abstract File write(T input) throws WacodisProcessingException;

    /**
     * Gets the class name that is supported for writing out input data.
     *
     * @return Class name that is supported for writing out input data.
     */
    public abstract String getSupportedClassName();
    
    /**
     * @return The File to which the input data will be
     * written.
     */
    public File getTargetFile() {
        return targetFile;
    }

}
