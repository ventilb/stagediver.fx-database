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

package de.iew.stagediver.fx.osgi;

import de.iew.stagediver.fx.database.provider.AbstractDBProviderFactory;
import de.iew.stagediver.fx.database.provider.DBProvider;
import de.iew.stagediver.fx.database.provider.DBProviderFactory;
import de.iew.stagediver.fx.database.provider.DBProviderFactoryException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

/**
 * Implements an OSGi managed {@link DBProviderFactory}. This factory can be configured with the
 * {@link ConfigurationAdmin}.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 22:32
 */
public class ManagedDBProviderFactory extends AbstractDBProviderFactory implements ManagedService {

    private static final Logger log = LoggerFactory.getLogger(ManagedDBProviderFactory.class);

    public static final String PID = DBProviderFactory.class.getName();

    private BundleContext bundleContext;

    public ManagedDBProviderFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public DBProvider newDBProvider() throws DBProviderFactoryException {
        try {
            final ServiceReference<ConfigurationAdmin> sr = this.bundleContext.getServiceReference(ConfigurationAdmin.class);
            final ConfigurationAdmin configurationAdmin = this.bundleContext.getService(sr);
            final Configuration config = configurationAdmin.getConfiguration(PID);

            return createDBProvider(config.getProperties());
        } catch (Exception e) {
            throw new DBProviderFactoryException(e);
        }
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        log.debug("ConnectionServiceImpl.update() called");
    }
}
