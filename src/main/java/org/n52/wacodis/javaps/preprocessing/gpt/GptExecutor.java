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
package org.n52.wacodis.javaps.preprocessing.gpt;

import java.util.Map;
import org.esa.snap.core.gpf.main.GPT;
import org.n52.wacodis.javaps.exceptions.WacodisProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for GPF graphs
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class GptExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GptExecutor.class);

    /**
     * Executes a GPF graph via GPT
     *
     * @param arguments {@link GptArguments} for executing the GPF graph via GPT
     * @throws WacodisProcessingException
     */
    public void executeGraph(GptArguments arguments) throws WacodisProcessingException {
        LOGGER.info("Execute processing graph: {}", arguments.getGraphFile());
        try {
            GPT.run(this.prepareArguments(arguments));
        } catch (Exception ex) {
            LOGGER.error("Processing graph execution unexpectedly terminated.", ex);
            throw new WacodisProcessingException("Error while executing processing graph.", ex);
        }
    }

    /**
     * Prepares the GPT arguments by creating an array of String arguments
     *
     * @param arguments {@link GptArguments} to prepare
     * @return Array of String arguments
     */
    public String[] prepareArguments(GptArguments arguments) {
        String[] args = new String[arguments.getParameters().size() + 2];
        int i = 0;
        args[i] = arguments.getGraphFile();
        args[++i] = "-e";

        for (Map.Entry<String, String> entry : arguments.getParameters().entrySet()) {
            args[++i] = "-P" + entry.getKey() + "=" + entry.getValue();
        }
        return args;
    }
}
