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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Parser for config files that define the execution of EO command line tools
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class ToolConfigParser {

    /**
     * Parses the given YAML config file
     *
     * @param path path to the YAML config file
     * @return {@link ToolConfig}
     * @throws IOException
     */
    public ToolConfig parse(String path) throws IOException {
        return this.parse(new FileInputStream(path));
    }

    /**
     * Parses the given YAML config file
     *
     * @param input {@link InputStream} for the YAML config file
     * @return {@link ToolConfig}
     * @throws IOException
     */
    public ToolConfig parse(InputStream input) throws IOException {
        Yaml yaml = new Yaml(new Constructor(ToolConfig.class));

        ToolConfig config = yaml.load(input);
        return config;
    }
}
