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

import java.util.ArrayList;
import java.util.List;

/**
 * converts AbstractProcessComand to ProcessBuilder needed to start a command as process
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CommandParser {

    private AbstractProcessCommand command;

    public AbstractProcessCommand getCommand() {
        return command;
    }

    public void setCommand(AbstractProcessCommand command) {
        this.command = command;
    }

    public CommandParser(AbstractProcessCommand command) {
        this.command = command;
    }
    
    public CommandParser(){}
    
    /**
     * creates ProcessBuilder for this.command
     * @return
     */
    public ProcessBuilder parseCommand() {
        List<String> parametersStr = new ArrayList<>();
        
        
        parametersStr.add(this.command.getProcessApplication());

        this.command.getParameter().forEach(p -> {
            
            //paramater might be a unnamed parameter (value only)
            if(p.getParameter() != null && !p.getParameter().trim().isEmpty()){
                parametersStr.add(p.getParameter());
            }

            //parameter might be a flag without value
            if (p.getValue() != null && !p.getValue().trim().isEmpty()) {
                parametersStr.add(p.getValue());
            }
        });

        handleWindowsOS(parametersStr);

        return new ProcessBuilder(parametersStr);
    }

    /**
     * windows needs to run command with cmd.exe
     *
     * @param parameters
     */
    private void handleWindowsOS(List<String> parameters) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            parameters.add(0, "cmd.exe");
            parameters.add(1, "/c");
        }
    }

}
