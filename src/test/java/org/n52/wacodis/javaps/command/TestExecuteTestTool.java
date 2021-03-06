/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
