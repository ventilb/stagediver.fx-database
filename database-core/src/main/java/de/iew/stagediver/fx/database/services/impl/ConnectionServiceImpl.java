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
import de.iew.stagediver.fx.database.provider.impl.HSQLDBProvider;
import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.exception.ConnectException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Implements an OSGi database connection service.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:38
 */
@Component(configurationPid = "de.iew.stagediver.fx.database.services.ConnectionService", createPid = false)
@Service(value = {ConnectionService.class, ManagedService.class})
public class ConnectionServiceImpl implements ConnectionService, ManagedService {

    private static final Logger log = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    public static final String PID = ConnectionService.class.getName();

    private final Hashtable<String, DataSource> datasources = new Hashtable<>();

    private final Set<Connection> connectionsInUse = new HashSet<>();

    private volatile boolean active = false;

    private BundleContext bundleContext;

    protected void activate(ComponentContext componentContext) {
        log.debug("ConnectionService was activated");

        this.bundleContext = componentContext.getBundleContext();
        this.active = true;
    }

    protected void deactivate(ComponentContext componentContext) {
        this.active = false;
        this.bundleContext = null;

        synchronized (this.connectionsInUse) {
            for (Connection connection : this.connectionsInUse) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("Error closing database connection '{}'", connection, e);
                }
            }
            this.connectionsInUse.clear();
            this.datasources.clear();
        }
        log.debug("ConnectionService was deactivated");
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        log.debug("ConnectionServiceImpl.update() called");
    }

    public Connection checkoutBuildInDatabaseConnection(final String catalogName, final String username, final String password) throws ConnectException {
        final boolean active = this.active;
        if (!active) {
            throw new IllegalStateException("The connection service is not active");
        }

        final DBProvider dbProvider;
        try {
            final ServiceReference<ConfigurationAdmin> sr = this.bundleContext.getServiceReference(ConfigurationAdmin.class);
            final ConfigurationAdmin configurationAdmin = this.bundleContext.getService(sr);
            final Configuration config = configurationAdmin.getConfiguration(PID);
            dbProvider = createDBProvider(config.getProperties());
        } catch (Exception e) {
            throw new ConnectException(e);
        }

        if (dbProvider == null) {
            throw new IllegalStateException("Database provider for build in database is not configured");
        }

        ComboPooledDataSource cpds = getOrCreateDataSource(dbProvider, catalogName, username, password);
        return obtainConnection(cpds);
    }

    protected Connection obtainConnection(ComboPooledDataSource cpds) throws ConnectException {
        Connection connection;
        try {
            connection = cpds.getConnection();
        } catch (SQLException e) {
            throw new ConnectException("Cannot obtain sql connection", e);
        }

        registerConnection(connection);
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

    protected void registerConnection(Connection connection) {
        final boolean active = this.active;
        if (!active) {
            /*
            Der Service ist nicht aktiv. Die gerade geöffnet Connection wird sofort wieder geschlossen.
             */
            try {
                log.info("The connection service is not active. Closing recently created connection.");
                connection.close();
            } catch (SQLException e) {
                log.error("Error closing connection", e);
            }
            throw new IllegalStateException("The connection service is not active");
        }

        synchronized (this.connectionsInUse) {
            this.connectionsInUse.add(connection);
        }
    }

    /**
     * Create a {@link de.iew.stagediver.fx.database.provider.DBProvider} instance according to the specified config.
     * Creates a default provider if config IS NULL or the configuration failed.
     *
     * @param config the config to use
     * @return the dB provider
     * @throws ConfigurationException thrown if the default configuration could not be applied
     */
    protected DBProvider createDBProvider(Dictionary<String, ?> config) throws ConfigurationException {
        if (config != null) {
            try {
                return createDBProviderFromConfiguration(config);
            } catch (ConfigurationException e) {
                log.error("Error configuring the connection service", e);
            }
        }
        return createDefaultDBProvider();
    }

    protected DBProvider createDefaultDBProvider() throws ConfigurationException {
        final Hashtable<String, String> config = new Hashtable<>();
        config.put(ConnectionService.DB_PROVIDER_CLASS, HSQLDBProvider.class.getName());
        config.put(HSQLDBProvider.DATABASE_PATH, System.getProperty("java.io.tmpdir"));
        config.put(HSQLDBProvider.DRIVER_CLASS, "org.hsqldb.jdbcDriver");

        return createDBProviderFromConfiguration(config);
    }

    protected DBProvider createDBProviderFromConfiguration(Dictionary<String, ?> config) throws ConfigurationException {
        final String providerClass = (String) config.get(DB_PROVIDER_CLASS);

        if (providerClass == null) {
            throw new ConfigurationException(DB_PROVIDER_CLASS, "The property is required");
        }

        Class aClass;
        try {
            aClass = Class.forName(providerClass);

            if (!DBProvider.class.isAssignableFrom(aClass)) {
                throw new ConfigurationException(DB_PROVIDER_CLASS, "Illegal class, not assignable from " + DBProvider.class.getName());
            }

        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(DB_PROVIDER_CLASS, "Class not found", e);
        }

        DBProvider instance;
        try {
            instance = (DBProvider) aClass.newInstance();
        } catch (Exception e) {
            throw new ConfigurationException(DB_PROVIDER_CLASS, "Cannot create db provider instance", e);
        }

        instance.configure(config);
        instance.verify();

        return instance;
    }
}
