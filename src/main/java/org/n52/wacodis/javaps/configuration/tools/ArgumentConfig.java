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
public class ArgumentConfig {

    private String name;
    private String type;
    private String value;
    private String quantity;
    private String separator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public String toString() {
        return "ArgumentConfig{" + "name=" + name + ", type=" + type
                + ", value=" + value + ", quantity=" + quantity + "separator=" + separator + '}';
    }

    public static enum TypeValues {

        WPS_PROCESS_REFERENCE {
            @Override
            public String getName() {
                return "wps-process-reference";
            }

        },
        STATIC_OPTION {
            @Override
            public String getName() {
                return "static-option";
            }

        };

        public static TypeValues forName(String name) {
            for (TypeValues v : TypeValues.values()) {
                if (v.getName().equals(name)) {
                    return v;
                }
            }

            throw new IllegalStateException("No value available for name: " + name);
        }

        public String getName() {
            return "";
        }

    }

    public static enum QuantityValues {

        SINGLE {
            @Override
            public String getName() {
                return "single";
            }

        },
        MULTIPLE {
            @Override
            public String getName() {
                return "multiple";
            }

        };

        public static TypeValues forName(String name) {
            for (TypeValues v : TypeValues.values()) {
                if (v.getName().equals(name)) {
                    return v;
                }
            }

            throw new IllegalStateException("No value available for name: " + name);
        }

        public String getName() {
            return "";
        }

    }

}
