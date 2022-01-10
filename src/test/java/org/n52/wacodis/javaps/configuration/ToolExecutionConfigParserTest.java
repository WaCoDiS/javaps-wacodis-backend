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
package org.n52.wacodis.javaps.configuration;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.wacodis.javaps.configuration.tools.ToolConfig;
import org.n52.wacodis.javaps.configuration.tools.ToolConfigParser;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class ToolExecutionConfigParserTest {

    @Test
    public void testConfigParsing() throws IOException {
        ToolConfigParser parser = new ToolConfigParser();
        ToolConfig config = parser.parse(ClassLoader.getSystemResourceAsStream("land-cover-classification-test.yml"));

        Assert.assertEquals("land-cover-classification", config.getId());
        
        Assert.assertEquals("unix:///var/run/docker.sock", config.getDocker().getHost());
        Assert.assertEquals("dlm_docker:wacodis-eo-hackathon", config.getDocker().getImage());
        Assert.assertEquals("wacodis-eo-dlm", config.getDocker().getContainer());
        Assert.assertEquals("/public", config.getDocker().getWorkDir());
        
        Assert.assertEquals("4326", config.getParameter().getInputEpsg());
        
        Assert.assertEquals("/bin/ash", config.getCommand().getFolder());
        Assert.assertEquals("/eo.sh", config.getCommand().getName());
        Assert.assertFalse(config.getCommand().getArguments().isEmpty());
    }

}
