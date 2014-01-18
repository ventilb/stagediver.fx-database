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

package de.iew.stagediver.fx.database.liquibase.impl;

import de.iew.stagediver.fx.database.liquibase.ResourceAccessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Bridge between our own and liquibase's resource accessors.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 18.01.14 - 19:29
 */
public class BundleResourceAccessorImpl implements ResourceAccessor, liquibase.resource.ResourceAccessor {

    private final BundleContext context;

    public BundleResourceAccessorImpl(BundleContext context) {
        this.context = context;
    }

    @Override
    public InputStream getResourceAsStream(String file) throws IOException {
        final URL url = this.context.getBundle().getEntry(file);
        return url.openStream();
    }

    @Override
    public Enumeration<URL> getResources(String packageName) throws IOException {
        final Bundle bundle = this.context.getBundle();
        return bundle.findEntries(packageName, null, true);
    }

    @Override
    public ClassLoader toClassLoader() {
        final Bundle bundle = this.context.getBundle();
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        return bundleWiring.getClassLoader();
    }
}
