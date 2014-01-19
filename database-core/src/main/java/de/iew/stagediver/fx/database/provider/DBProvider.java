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

import org.osgi.service.cm.ConfigurationException;

import java.util.Dictionary;

/**
 * Specifies an interface for database providers. Providers are used to abstract the configuration of the database
 * systems out there.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 19.01.14 - 01:19
 */
public interface DBProvider {

    /**
     * Configures this db provider according to the specified properties. The required properties depends on the
     * database this provider serves.
     *
     * @param properties the properties
     */
    public void configure(Dictionary<String, ?> properties);

    /**
     * Verifies that this db provider was successfully configured and is ready to use.
     *
     * @throws ConfigurationException thrown is any property is not in an expected state
     */
    public void verify() throws ConfigurationException;

    /**
     * Creates the database url to connect to the configured database system.
     *
     * @param catalogName the catalog name to connect to
     * @return the database url
     */
    public String createDatabaseConnectionUrl(String catalogName);

    /**
     * Returns the JDBC driver class name.
     *
     * @return the driver class
     */
    public String getDriverClass();
}
