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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CommandParserTest {

    public CommandParserTest() {
    }

    /**
     * Test of parseCommand method, of class CommandParser.
     */
    @Test
    public void testParseCommand() {
        ProcessCommand pc = new ProcessCommand("mvn");

        CommandParameter p1 = new CommandParameter();
        p1.setParameter("-version");
        pc.addParameter(p1);

        CommandParser cp = new CommandParser(pc);
        ProcessBuilder pb = cp.parseCommand();

        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            assertEquals("cmd.exe /c mvn -version", String.join(" ", pb.command()));
        } else {
            assertEquals("mvn -version", String.join(" ", pb.command()));
        }
    }

    @Test
    public void testParseCommand_emptyParameterValue() {
        ProcessCommand pc = new ProcessCommand("mvn");

        CommandParameter p1 = new CommandParameter("-version", "");
        pc.addParameter(p1);

        CommandParser cp = new CommandParser(pc);
        ProcessBuilder pb = cp.parseCommand();

        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            assertEquals("cmd.exe /c mvn -version", String.join(" ", pb.command()));
        } else {
            assertEquals("mvn -version", String.join(" ", pb.command()));
        }
    }

}
