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

package de.iew.stagediver.fx.database.services.impl;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.exception.ConnectException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Implements an OSGi database connection service.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:38
 */
@Component
@Service(value = {ConnectionService.class})
public class ConnectionServiceImpl implements ConnectionService {

    private static Logger log = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    private final Hashtable<String, Object> datasources = new Hashtable<String, Object>();

    private final Set<Connection> connectionsInUse = new HashSet<Connection>();

    protected static final String buildInDatabaseDriverClass = "org.hsqldb.jdbcDriver";

    protected void activate(ComponentContext componentContext) {
        log.debug("ConnectionService was activated");
    }

    protected void deactivate(ComponentContext componentContext) {
        log.debug("ConnectionService was deactivated");

        synchronized (this.connectionsInUse) {
            for (Connection connection : this.connectionsInUse) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("Error closing database connection '{}'", connection, e);
                }
            }
            this.connectionsInUse.clear();
        }
    }

    public Connection checkoutBuildInDatabaseConnection(final String databaseName, final String username, final String password) throws ConnectException {

        final String databaseUrl = "jdbc:hsqldb:file:/home/manuel/" + databaseName;
        final String databaseDriverClass = buildInDatabaseDriverClass;

        try {
            ComboPooledDataSource cpds;
            synchronized (this.datasources) {
                if (this.datasources.containsKey(databaseUrl)) {
                    cpds = (ComboPooledDataSource) this.datasources.get(databaseUrl);
                } else {
                    cpds = new ComboPooledDataSource();
                    try {
                        cpds.setDriverClass(databaseDriverClass);
                    } catch (PropertyVetoException e) {
                        throw new ConnectException("Failed to configure driver class '" + databaseDriverClass + "'.", e);
                    }
                    cpds.setJdbcUrl(databaseUrl);
                    cpds.setUser(username);
                    cpds.setPassword(password);
                    cpds.setMinPoolSize(5);
                    cpds.setAcquireIncrement(5);
                    cpds.setMaxPoolSize(20);

                    this.datasources.put(databaseUrl, cpds);
                }
            }

            // TODO Wir sollten bei deactivate ein Flag setzen und hier dann keine Connection mehr anmelden
            Connection connection = cpds.getConnection();
            registerConnection(connection);
            return connection;
        } catch (Exception e) {
            throw new ConnectException("Failed to obtain a database connection to the build in database for database name '" + databaseName + "'.", e);
        }
    }

    protected void registerConnection(Connection connection) {
        synchronized (this.connectionsInUse) {
            this.connectionsInUse.add(connection);
        }
    }
}
