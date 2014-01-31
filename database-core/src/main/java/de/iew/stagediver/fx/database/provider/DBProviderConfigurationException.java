/*
 * Copyright 2012-2014 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.stagediver.fx.database.provider;

/**
 * An exception to indicate errors in the configuration of a {@link DBProvider}. The exception will carry the property
 * which caused this exception for further processing.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 22:39
 */
public class DBProviderConfigurationException extends Exception {

    private final String property;

    public DBProviderConfigurationException(String property, String message) {
        this(property, message, null);
    }

    public DBProviderConfigurationException(String property, String message, Throwable cause) {
        super(message, cause);
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
