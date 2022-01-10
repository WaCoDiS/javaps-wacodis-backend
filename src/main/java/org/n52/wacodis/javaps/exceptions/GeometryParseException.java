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
package org.n52.wacodis.javaps.exceptions;

/**
 * An exception that will be thrown if a geometry can not be parsed out of the String that includes the geometry.
 *
 * @author @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class GeometryParseException extends Exception {

    public GeometryParseException() {
    }

    public GeometryParseException(String message, String expectedFormat) {
        super(String.format("%s\nExpected format: %s", message, expectedFormat));
    }

    public GeometryParseException(String message, String expectedFormat, Throwable cause) {
        super(String.format("%s\nExpected format: %s", message, expectedFormat), cause);
    }

    public GeometryParseException(String message) {
        super(message);
    }

    public GeometryParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeometryParseException(Throwable cause) {
        super(cause);
    }
}
