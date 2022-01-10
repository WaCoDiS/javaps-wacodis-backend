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

import java.util.Arrays;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class TestExecuteTestTool {
    
    public TestExecuteTestTool() {
    }

    /**
     * Test of execute method, of class ProcessCommand.
     */
    @Test
    @Ignore
    public void testExecute() throws Exception {
        String application = "docker run";
        CommandParameter name = new CommandParameter("--name", "wacodis-eo-dlm");
        CommandParameter volume = new CommandParameter("-v", "C:\\Users\\Arne\\Desktop\\wacodis\\public:/public");
        CommandParameter interactive = new CommandParameter("-i", ""); //flag
        CommandParameter remove = new CommandParameter("--rm", ""); //fag
        CommandParameter image = new CommandParameter("", "dlm_docker:wacodis-eo-hackathon"); //unnamed parameter
        CommandParameter command = new CommandParameter("bin/ash", "/eo.sh");
        CommandParameter command_input = new CommandParameter("-input", "S2B_MSIL2A_20181010T104019_N0209_R008_T32ULB_20181010T171128.tif");
        CommandParameter command_result = new CommandParameter("-result", "result.tif");
        CommandParameter command_trainig = new CommandParameter("-training", "traindata/wacodis_traindata");
        
        AbstractProcessCommand cmd = new ProcessCommand(application);
        cmd.setParameter(Arrays.asList(new CommandParameter[]{name, volume, interactive, remove, image, command, command_input, command_result, command_trainig}));
        
        CommandParser testParser = new CommandParser(cmd);
        System.out.println(String.join(" ", testParser.parseCommand().command()));
        
        ProcessResult pr = cmd.execute();
        System.out.println("result code: " + pr.getResultCode());
        System.out.println("output message: " + pr.getOutputMessage());
    }
    
}
