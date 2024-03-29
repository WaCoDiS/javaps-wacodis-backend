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

import java.util.List;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class CommandConfig {

    private String folder;
    private String name;
    private List<ArgumentConfig> arguments;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArgumentConfig> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentConfig> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "CommandConfig{" + "folder=" + folder + ", name="
                + name + ", arguments=" + arguments + '}';
    }

}
