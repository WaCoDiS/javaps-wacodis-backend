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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.n52.wacodis.javaps.command.CommandParameter;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DockerRunCommandConfiguration {

    private final List<String> portBindings;
    private final List<String> volumeBindings;
    private final List<CommandParameter> commandParameters;

    public DockerRunCommandConfiguration() {
        this.portBindings = new ArrayList<>();
        this.volumeBindings = new ArrayList<>();
        this.commandParameters = new ArrayList();
    }

    public boolean addVolumeBinding(String volumeBinding) {
        return this.volumeBindings.add(volumeBinding);
    }

    public boolean removeVolumeBinding(String volumeBinding) {
        return this.volumeBindings.remove(volumeBinding);
    }

    public boolean addPortBinding(String portBinding) {
        return this.portBindings.add(portBinding);
    }

    public boolean removePortBinding(String portBinding) {
        return this.portBindings.remove(portBinding);
    }

    public boolean addCommandParameter(CommandParameter parameter) {
        return this.commandParameters.add(parameter);
    }

    public boolean removeCommandParameter(CommandParameter parameter) {
        return this.commandParameters.remove(parameter);
    }

    public void addCommandParameter(int index, CommandParameter parameter) {
        this.commandParameters.add(index, parameter);
    }

    public CommandParameter removeCommandParameter(int index) {
        return this.commandParameters.remove(index);
    }

    public List<CommandParameter> getCommandParameters() {
        return Collections.unmodifiableList(this.commandParameters);
    }

    public List<String> getPortBindings() {
        return Collections.unmodifiableList(this.portBindings);
    }

    public List<String> getVolumeBindings() {
        return Collections.unmodifiableList(this.volumeBindings);
    }

}
