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

import de.iew.stagediver.fx.database.Constants;
import de.iew.stagediver.fx.database.provider.impl.HSQLDBProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Abstract implementation of the {@link DBProviderFactory} interface with common helper functionality.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 22:24
 */
public abstract class AbstractDBProviderFactory implements DBProviderFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractDBProviderFactory.class);

    /**
     * Create a {@link de.iew.stagediver.fx.database.provider.DBProvider} instance according to the specified config.
     * Creates a default provider if config IS NULL or the configuration failed.
     *
     * @param config the config to use
     * @return the dB provider
     * @throws DBProviderConfigurationException thrown if the default configuration could not be applied
     */
    protected DBProvider createDBProvider(Dictionary<String, ?> config) throws DBProviderConfigurationException {
        if (config != null) {
            try {
                return createDBProviderFromConfiguration(config);
            } catch (DBProviderConfigurationException e) {
                log.error("Error creating a db provider from configuration. Falling back to the default db provider.", e);
            }
        }
        return createDefaultDBProvider();
    }

    protected DBProvider createDefaultDBProvider() throws DBProviderConfigurationException {
        final Hashtable<String, String> config = new Hashtable<>();
        config.put(HSQLDBProvider.DATABASE_BACKEND, "file");
        config.put(Constants.DB_PROVIDER_CLASS, HSQLDBProvider.class.getName());
        config.put(HSQLDBProvider.DATABASE_PATH, System.getProperty("java.io.tmpdir"));
        config.put(HSQLDBProvider.DRIVER_CLASS, "org.hsqldb.jdbcDriver");

        return createDBProviderFromConfiguration(config);
    }

    protected DBProvider createDBProviderFromConfiguration(Dictionary<String, ?> config) throws DBProviderConfigurationException {
        final String providerClass = (String) config.get(Constants.DB_PROVIDER_CLASS);

        if (providerClass == null) {
            throw new DBProviderConfigurationException(Constants.DB_PROVIDER_CLASS, "The property is required");
        }

        Class aClass;
        try {
            aClass = Class.forName(providerClass);

            if (!DBProvider.class.isAssignableFrom(aClass)) {
                throw new DBProviderConfigurationException(Constants.DB_PROVIDER_CLASS, "Illegal class, not assignable from " + DBProvider.class.getName());
            }

        } catch (ClassNotFoundException e) {
            throw new DBProviderConfigurationException(Constants.DB_PROVIDER_CLASS, "Class not found", e);
        }

        DBProvider instance;
        try {
            instance = (DBProvider) aClass.newInstance();
        } catch (Exception e) {
            throw new DBProviderConfigurationException(Constants.DB_PROVIDER_CLASS, "Cannot create db provider instance", e);
        }

        instance.configure(config);
        instance.verify();

        return instance;
    }


}
