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

import liquibase.osgi.OSGIPackageScanClassResolver;
import liquibase.servicelocator.CustomResolverServiceLocator;
import liquibase.servicelocator.PackageScanClassResolver;
import liquibase.servicelocator.ServiceLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Implements the bundle activator for the database OSGi bundle.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 22:34
 */
public class DatabaseBundleActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        final PackageScanClassResolver resolver = new OSGIPackageScanClassResolver(context.getBundle());
        ServiceLocator.setInstance(new CustomResolverServiceLocator(resolver));
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
