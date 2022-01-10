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
package org.n52.wacodis.javaps.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public interface InputDataPreprocessor<T> {

    /**
     * Applies preprocessing on input data and stores the preprocessed at
     *
     * @param input input data
     * @param outputDirectoryPath path to the directory for storing the
     * preprocessed data
     * @return list of preprocessed files
     * @throws IOException
     */
    public List<File> preprocess(T input, String outputDirectoryPath) throws WacodisProcessingException;
}