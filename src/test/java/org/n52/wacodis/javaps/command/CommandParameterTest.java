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
public class CommandParameterTest {
    
    public CommandParameterTest() {}

    /**
     * Test of toString method, of class CommandParameter.
     */
    @Test
    public void testToString() {
        CommandParameter testParam = new CommandParameter("-key", "value");
        CommandParameter unamedParam = new CommandParameter("", "value");
        CommandParameter flag = new CommandParameter("-key", "");
        
        assertEquals("-key value", testParam.toString());
        assertEquals("value", unamedParam.toString());
        assertEquals("-key", flag.toString());
    }
    
    
}
