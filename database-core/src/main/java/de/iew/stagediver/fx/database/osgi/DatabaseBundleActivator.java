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

package de.iew.stagediver.fx.database.osgi;

import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.LiquibaseService;
import de.iew.stagediver.fx.database.services.impl.ConnectionServiceImpl;
import de.iew.stagediver.fx.database.services.impl.LiquibaseServiceImpl;
import liquibase.osgi.OSGIPackageScanClassResolver;
import liquibase.servicelocator.CustomResolverServiceLocator;
import liquibase.servicelocator.PackageScanClassResolver;
import liquibase.servicelocator.ServiceLocator;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the bundle activator for the database OSGi bundle.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:34
 */
public class DatabaseBundleActivator implements BundleActivator, ServiceListener {
    private static final Logger log = LoggerFactory.getLogger(DatabaseBundleActivator.class);

    private ManagedDBProviderFactory dbProviderFactory;

    private ConnectionServiceImpl connectionService;

    private LiquibaseServiceImpl liquibaseService;

    private ServiceRegistration<ConnectionService> connectionServiceServiceRegistration;

    private ServiceRegistration<LiquibaseService> liquibaseServiceServiceRegistration;

    @Override
    public void start(final BundleContext context) throws Exception {
        final PackageScanClassResolver resolver = new OSGIPackageScanClassResolver(context.getBundle());
        ServiceLocator.setInstance(new CustomResolverServiceLocator(resolver));

        final ServiceReference<ConfigurationAdmin> configurationAdminServiceReference = context.getServiceReference(ConfigurationAdmin.class);
        if (configurationAdminServiceReference != null) {
            initialise(context, configurationAdminServiceReference);
        }

        final String filter = String.format("(objectClass=%s)", ConfigurationAdmin.class.getName());
        context.addServiceListener(this, filter);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        unregisterServices();
        this.dbProviderFactory = null;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        final ServiceReference<ConfigurationAdmin> serviceReference = (ServiceReference<ConfigurationAdmin>) event.getServiceReference();
        final Bundle bundle = FrameworkUtil.getBundle(DatabaseBundleActivator.class);
        final BundleContext bundleContext = bundle.getBundleContext();
        switch (event.getType()) {
            case ServiceEvent.REGISTERED:
                initialise(bundleContext, serviceReference);
                break;
            case ServiceEvent.UNREGISTERING:
                unregisterServices();

                this.dbProviderFactory = null;
                break;
        }
    }

    protected void initialise(final BundleContext bundleContext, ServiceReference<ConfigurationAdmin> configurationAdminServiceReference) {
        if (this.dbProviderFactory != null) {
            log.warn("The dbProviderFactory property is already initialised.");
            return;
        }

        if (configurationAdminServiceReference != null) {
            final ConfigurationAdmin configurationAdmin = bundleContext.getService(configurationAdminServiceReference);
            this.dbProviderFactory = new ManagedDBProviderFactory(configurationAdmin);

            registerServices(bundleContext);
        }
    }

    protected void registerServices(final BundleContext bundleContext) {
        if (this.dbProviderFactory != null) {
            this.connectionService = new ConnectionServiceImpl();
            this.connectionService.setDbProviderFactory(this.dbProviderFactory);

            this.liquibaseService = new LiquibaseServiceImpl();

            this.connectionServiceServiceRegistration = bundleContext.registerService(ConnectionService.class, this.connectionService, null);
            this.liquibaseServiceServiceRegistration = bundleContext.registerService(LiquibaseService.class, this.liquibaseService, null);
        }
    }

    protected void unregisterServices() {
        if (this.liquibaseServiceServiceRegistration != null) {
            this.liquibaseServiceServiceRegistration.unregister();
            this.liquibaseServiceServiceRegistration = null;
            this.liquibaseService = null;
        }

        if (this.connectionServiceServiceRegistration != null) {
            this.connectionServiceServiceRegistration.unregister();
            this.connectionServiceServiceRegistration = null;
            this.connectionService = null;
        }
    }
}
