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

import com.github.dockerjava.api.command.CreateContainerResponse;
import org.n52.wacodis.javaps.command.ProcessResult;
import org.n52.wacodis.javaps.command.ToolExecutionProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * runs docker container
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DockerProcess implements ToolExecutionProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerProcess.class);

    private DockerController dockerController;
    private DockerContainer container;
    private DockerRunCommandConfiguration containerRunConfig;

    public DockerProcess(DockerController dockerController, DockerContainer container, DockerRunCommandConfiguration containerRunConfig) {
        this.dockerController = dockerController;
        this.container = container;
        this.containerRunConfig = containerRunConfig;
    }

    public DockerContainer getContainer() {
        return container;
    }

    public void setContainer(DockerContainer container) {
        this.container = container;
    }

    public DockerRunCommandConfiguration getContainerRunConfig() {
        return containerRunConfig;
    }

    public void setContainerRunConfig(DockerRunCommandConfiguration containerRunConfig) {
        this.containerRunConfig = containerRunConfig;
    }

    public DockerController getDockerController() {
        return dockerController;
    }

    public void setDockerController(DockerController dockerController) {
        this.dockerController = dockerController;
    }

    /**
     * execute docker run command synchronously,
     * thread blocks until executed container dies (stopped, finished),
     * container is removed after execution
     * @return ProcessResult containing container exit code and container log
     * @throws InterruptedException 
     */
    @Override
    public ProcessResult execute() throws InterruptedException {
        //create container
        CreateContainerResponse createdContainer = this.dockerController.createDockerContainer(this.container, this.containerRunConfig);
        String containerID = createdContainer.getId();
        
        //run container synchronously, remove container after execution
        try{        
            ProcessResult results = this.dockerController.runDockerContainer_Sync(containerID);
            return results;
        }finally{
            this.dockerController.removeDockerContainer(containerID); //make sure container is removed in any case
        }
    }
}
