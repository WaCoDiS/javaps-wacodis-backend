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
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public abstract class AbstractProcessCommand implements ToolExecutionProcess{

    private String processApplication;

    private List<CommandParameter> parameters;

    public AbstractProcessCommand(String processApplication) {
        this.parameters = new ArrayList<>();
        this.processApplication = processApplication;
    }

    @Override
    public abstract ProcessResult execute() throws InterruptedException;

    public String getProcessApplication() {
        return processApplication;
    }

    public void setProcessApplication(String processApplication) {
        this.processApplication = processApplication;
    }

    public List<CommandParameter> getParameter() {
        return parameters;
    }

    public void setParameter(List<CommandParameter> parameter) {
        this.parameters = parameter;
    }

    public void addParameter(CommandParameter param) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        this.parameters.add(param);
    }

}
