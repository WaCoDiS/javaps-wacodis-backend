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
package org.n52.wacodis.javaps.configuration.tools;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class ToolConfig {

    private String id;
    private DockerConfig docker;
    private CommandConfig command;
    private ParameterConfig parameter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DockerConfig getDocker() {
        return docker;
    }

    public void setDocker(DockerConfig docker) {
        this.docker = docker;
    }

    public CommandConfig getCommand() {
        return command;
    }

    public void setCommand(CommandConfig command) {
        this.command = command;
    }

    public ParameterConfig getParameter() {
        return parameter;
    }

    public void setParameter(ParameterConfig parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "ToolConfig{" + "id=" + id + ", docker=" + docker + ", command="
                + command + ", parameter=" + parameter + '}';
    }

}
