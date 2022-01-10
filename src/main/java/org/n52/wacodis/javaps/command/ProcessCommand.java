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
package org.n52.wacodis.javaps.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class ProcessCommand extends AbstractProcessCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCommand.class);

    public ProcessCommand(String processApplication) {
        super(processApplication);
    }

    @Override
    public ProcessResult execute() throws InterruptedException {
        CommandParser parser = new CommandParser(this);
        int returnCode = -1;

        ProcessResult result = new ProcessResult(returnCode, "");
        try {
            Process process = parser.parseCommand().start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    StrBuilder builder = new StrBuilder();

                    try {
                        while ((line = input.readLine()) != null) {
                            builder.appendln(line);
                        }
                        result.setOutputMessage(builder.toString());
                        LOGGER.debug(result.getOutputMessage());

                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                        LOGGER.debug("Error while reading process output", ex);
                    }
                }
            }).start();

            returnCode = process.waitFor();
            result.setResultCode(returnCode);

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while prcessing command", ex);
        } finally {
            return result;
        }
    }

}
