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
import de.iew.stagediver.fx.database.provider.DBProvider;
import de.iew.stagediver.fx.database.provider.DBProviderFactory;
import de.iew.stagediver.fx.database.provider.DBProviderFactoryException;
import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.exception.ConnectException;
import de.iew.stagediver.fx.database.services.exception.ConnectionReleaseException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Implements an OSGi database connection service.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:38
 */
@Singleton
public class ConnectionServiceImpl implements ConnectionService {

    // TODO spezielle Registry für die Connections bauen
    private final Hashtable<String, DataSource> datasources = new Hashtable<>();

    private final Set<Connection> connectionsInUse = new HashSet<>();

    @Override
    public Connection checkoutBuildInDatabaseConnection(final String catalogName, final String username, final String password) throws ConnectException {
        final DBProvider dbProvider;
        try {
            dbProvider = this.dbProviderFactory.newDBProvider();
        } catch (DBProviderFactoryException e) {
            throw new ConnectException(e);
        }

        if (dbProvider == null) {
            throw new ConnectException("Database provider for build in database is not configured");
        }

        ComboPooledDataSource cpds = getOrCreateDataSource(dbProvider, catalogName, username, password);
        return obtainConnection(cpds);
    }

    @Override
    public void releaseConnection(final Connection connection) throws ConnectionReleaseException {
        try {
            if (unregisterConnectionInUse(connection)) {
                connection.close();
            } else {
                throw new ConnectionReleaseException("The connection was not unregistered");
            }
        } catch (SQLException e) {
            throw new ConnectionReleaseException(e);
        }
    }

    protected Connection obtainConnection(ComboPooledDataSource cpds) throws ConnectException {
        Connection connection;
        try {
            connection = cpds.getConnection();
        } catch (SQLException e) {
            throw new ConnectException("Cannot obtain sql connection", e);
        }

        registerConnectionInUse(connection);
        return connection;
    }

    protected ComboPooledDataSource getOrCreateDataSource(DBProvider dbProvider, final String databaseName, final String username, final String password) throws ConnectException {
        final String databaseUrl = dbProvider.createDatabaseConnectionUrl(databaseName);
        final String databaseDriverClass = dbProvider.getDriverClass();

        ComboPooledDataSource cpds;
        try {
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
                    // TODO Konfiguration erlauben
                    cpds.setJdbcUrl(databaseUrl);
                    cpds.setUser(username);
                    cpds.setPassword(password);
                    cpds.setMinPoolSize(5);
                    cpds.setAcquireIncrement(5);
                    cpds.setMaxPoolSize(20);

                    this.datasources.put(databaseUrl, cpds);
                }
            }
        } catch (Exception e) {
            throw new ConnectException("Failed to obtain a database connection to the build in database for database name '" + databaseName + "'.", e);
        }

        return cpds;
    }

    protected void registerConnectionInUse(final Connection connection) {
        synchronized (this.connectionsInUse) {
            this.connectionsInUse.add(connection);
        }
    }

    protected boolean unregisterConnectionInUse(final Connection connection) {
        synchronized (this.connectionsInUse) {
            return this.connectionsInUse.remove(connection);
        }
    }

    // Dependencies ///////////////////////////////////////////////////////////

    private DBProviderFactory dbProviderFactory;

    @Inject
    public void setDbProviderFactory(DBProviderFactory dbProviderFactory) {
        this.dbProviderFactory = dbProviderFactory;
    }

}
