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

package de.iew.stagediver.fx.database.services;

import de.iew.stagediver.fx.database.services.exception.ConnectException;

import java.sql.Connection;

/**
 * Specifies a service interface to implement services for obtaining connections to databases.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:38
 */
public interface ConnectionService {

    /**
     * Checkout of a connection to the build in database.
     *
     * @param databaseName the database name
     * @param username     the username
     * @param password     the password
     * @return the connection
     * @throws ConnectException if the connect failed
     */
    public Connection checkoutBuildInDatabaseConnection(final String databaseName, final String username, final String password) throws ConnectException;

}
