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
package org.n52.wacodis.javaps.command.docker;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.n52.wacodis.javaps.command.ProcessResult;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DockerProcessIT {
    
    public DockerProcessIT() {
    }


    /**
     * Test of execute method, of class DockerProcess.
     * @throws java.lang.Exception
     */
    @Test
    @Ignore
    public void testExecute() throws Exception {
        //controller with default connection
        DockerController controller = new DockerController();
        
        DockerContainer container = new DockerContainer("hello-world-container", "hello-world:latest");
        DockerRunCommandConfiguration config = new DockerRunCommandConfiguration();
        
        DockerProcess helloWorldProcess = new DockerProcess(controller, container, config);
        ProcessResult results = helloWorldProcess.execute();
        
        System.out.println("run docker container 1: "+ System.lineSeparator() + "exitCode: " + results.getResultCode()+ " log: " + System.lineSeparator() + results.getOutputMessage());
        
        //run twice to see if container was removed
        results = helloWorldProcess.execute();
        System.out.println("run docker container 2: "+ System.lineSeparator() + "exitCode: " + results.getResultCode()+ " log: " + System.lineSeparator() + results.getOutputMessage());
    }
    
}
