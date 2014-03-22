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

package de.iew.stagediver.fx.database.provider.impl;

import de.iew.stagediver.fx.database.provider.DBProvider;
import de.iew.stagediver.fx.database.provider.DBProviderConfigurationException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a database provider for a HSQLDB database.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 19.01.14 - 01:20
 */
public class HSQLDBProvider implements DBProvider {

    public static final String DATABASE_BACKEND = "hsqldb.database.backend";
    public static final String DATABASE_PATH = "hsqldb.database.path";
    public static final String DRIVER_CLASS = "hsqldb.driver.class";

    /**
     * The supported HSQLDB backends (storage engines).
     *
     * @see <a href="http://hsqldb.org/doc/guide/dbproperties-chapt.html">http://hsqldb.org/doc/guide/dbproperties-chapt.html</a>
     */
    // , "res", "hsql", "hsqls", "http", "https" currently not supported. Requires configuration of host and port or resource path
    protected static final Set<String> SUPPORTED_HSQLDB_BACKENDS = new HashSet<>(Arrays.asList("file", "mem"));

    private String databaseBackend;

    private String databasePath;

    private String driverClass;

    @Override
    public void configure(Dictionary<String, ?> properties) {
        // We don't predefine any defaults here. It's up to the factory to create and initialise the provider in the
        // correct way.
        this.databaseBackend = (String) properties.get(DATABASE_BACKEND);
        this.databasePath = (String) properties.get(DATABASE_PATH);
        this.driverClass = (String) properties.get(DRIVER_CLASS);
    }

    @Override
    public void verify() throws DBProviderConfigurationException {
        validateConfiguredDatabaseBackend();
        validateConfiguredDatabasePath();

        if (StringUtils.isBlank(this.driverClass)) {
            throw new DBProviderConfigurationException(DRIVER_CLASS, "The property can't be empty");
        }
    }

    @Override
    public String createDatabaseConnectionUrl(final String catalogName) {
        if (StringUtils.isBlank(catalogName)) {
            throw new IllegalArgumentException("catalogName can't be empty");
        }

        if (!this.databasePath.endsWith(File.separator)) {
            this.databasePath = this.databasePath + File.separator;
        }

        return String.format("jdbc:hsqldb:%s:%s%s", this.databaseBackend, this.databasePath, catalogName);
    }

    public boolean isSupportedHsqlBackend(final String hsqlBackendToTest) {
        return StringUtils.isNotBlank(hsqlBackendToTest) && SUPPORTED_HSQLDB_BACKENDS.contains(hsqlBackendToTest);
    }

    /**
     * Validates the configured database backend. The database backend is valid if it is listed in the
     * {@link #SUPPORTED_HSQLDB_BACKENDS} set. This method does not check backend specific configuration.
     *
     * @throws DBProviderConfigurationException if the validation failed
     */
    public void validateConfiguredDatabaseBackend() throws DBProviderConfigurationException {
        if (!isSupportedHsqlBackend(this.databaseBackend)) {
            throw new DBProviderConfigurationException(DATABASE_BACKEND, "The database backend is not supported by this provider implementation");
        }
    }

    /**
     * Validates the configured database file system path. Does not have any effect if a path is not configured. If
     * configured the path must be a writeable directory.
     *
     * @throws DBProviderConfigurationException if the validation failed
     */
    public void validateConfiguredDatabasePath() throws DBProviderConfigurationException {
        // The database path could also be reused for the res-backend but then requires a backend specific validation
        // which is not yet implemented.
        if (StringUtils.isNotBlank(this.databasePath)) {
            File databasePathTest = new File(this.databasePath);

            if (!(databasePathTest.isDirectory() && databasePathTest.canWrite())) {
                throw new DBProviderConfigurationException(DATABASE_PATH, "The path is not a directory or not writeable");
            }
        }
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public String getDriverClass() {
        return driverClass;
    }

}
