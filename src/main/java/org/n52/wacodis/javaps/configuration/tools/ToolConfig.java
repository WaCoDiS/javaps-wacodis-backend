/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
