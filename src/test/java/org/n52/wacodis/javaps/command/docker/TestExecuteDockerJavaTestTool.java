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
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import com.github.dockerjava.core.command.EventsResultCallback;
import java.util.concurrent.CountDownLatch;
import org.junit.Ignore;
import org.junit.Test;
import org.n52.wacodis.javaps.command.CommandParameter;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @author <a href="mailto:adrian.klink@eftas.com">Adrian Klink</a>
 */
public class TestExecuteDockerJavaTestTool {

    public TestExecuteDockerJavaTestTool() {
    }

    /**
     * Test of execute method, of class DockerController.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    @Ignore
    public void testExecute() throws InterruptedException {

        DockerController dockCont = new org.n52.wacodis.javaps.command.docker.DockerController();

        DockerContainer container = new DockerContainer("wacodis-eo-dlm", "dlm_docker:wacodis-eo-hackathon");
        DockerRunCommandConfiguration runConfig = new DockerRunCommandConfiguration();

        String volumemapping = "/home/ak/Develop/wacodis/public:/public"; //linux
        //String volumemapping = "C:\\Users\\Mustermann\\path\\to\\wacodis\\public:/public"; //windows
        runConfig.addVolumeBinding(volumemapping);

        runConfig.addCommandParameter(new CommandParameter("", "/bin/ash"));
        runConfig.addCommandParameter(new CommandParameter("", "/eo.sh"));
        runConfig.addCommandParameter(new CommandParameter("-input", "S2B_MSIL2A_20181010T104019_N0209_R008_T32ULB_20181010T171128.tif"));
        runConfig.addCommandParameter(new CommandParameter("-result", "result.tif"));
        runConfig.addCommandParameter(new CommandParameter("-training", "traindata/wacodis_traindata"));

        CountDownLatch cdl = new CountDownLatch(1); //sync thread until container dies

        CreateContainerResponse createdContainer = dockCont.createDockerContainer(container, runConfig);
        String createdContainerID = createdContainer.getId();
        dockCont.runDockerContainer(createdContainerID, new DieCallback(dockCont, createdContainerID, cdl));
        InspectContainerResponse inspectedContainer = dockCont.inspectDockerContainer(createdContainerID);
        System.out.println(inspectedContainer.getLogPath());
        //dockCont.stopDockerContainer(createdContainerID);
        String log = dockCont.retrieveDockerContainerLog_Sync(createdContainerID);
        System.out.println("Log for container " + createdContainerID + ": " + log);
        cdl.await(); //wait until container dies (stopped, finished)
    }

    private class DieCallback extends EventsResultCallback {

        private final DockerController controller;
        private final String containerID;
        private final CountDownLatch countDownLatch;

        public DieCallback(DockerController controller, String containerID, CountDownLatch countDownLatch) {
            this.controller = controller;
            this.containerID = containerID;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onNext(Event event) {
            if (event.getType().equals(EventType.CONTAINER) && event.getAction().equals("die")) {
                System.out.println(event.toString());
                System.out.println("remove Container: " + this.containerID);
                this.controller.removeDockerContainer(this.containerID); //remove container when dead
                countDownLatch.countDown();
            }
        }
    }

}
