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
import org.apache.commons.lang.StringUtils;
import org.osgi.service.cm.ConfigurationException;

import java.io.File;
import java.util.Dictionary;

/**
 * Implements a database provider for a HSQLDB database.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 19.01.14 - 01:20
 */
public class HSQLDBProvider implements DBProvider {

    public static final String DATABASE_PATH = "hsqldb.database.path";
    public static final String DRIVER_CLASS = "hsqldb.driver.class";

    private String databasePath;

    private String driverClass;

    @Override
    public void configure(Dictionary<String, ?> properties) {
        this.databasePath = (String) properties.get(DATABASE_PATH);
        this.driverClass = (String) properties.get(DRIVER_CLASS);
    }

    @Override
    public void verify() throws ConfigurationException {
        if (StringUtils.isBlank(this.databasePath)) {
            throw new ConfigurationException(DATABASE_PATH, "The property can't be empty");
        }

        if (StringUtils.isBlank(this.databasePath)) {
            throw new ConfigurationException(DRIVER_CLASS, "The property can't be empty");
        }

        File databasePathTest = new File(this.databasePath);

        if (!(databasePathTest.isDirectory() && databasePathTest.canWrite())) {
            throw new ConfigurationException(DATABASE_PATH, "The path is not a directory or not writeable");
        }
    }

    @Override
    public String createDatabaseConnectionUrl(String catalogName) {
        if (StringUtils.isBlank(catalogName)) {
            throw new IllegalArgumentException("catalogName can't be empty");
        }

        if (!this.databasePath.endsWith(File.separator)) {
            this.databasePath = this.databasePath + File.separator;
        }

        return String.format("jdbc:hsqldb:file:%s%s", this.databasePath, catalogName);
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public String getDriverClass() {
        return driverClass;
    }

}
