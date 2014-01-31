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

package de.iew.stagediver.fx.database.nonosgi;

import de.iew.stagediver.fx.database.provider.DBProviderFactory;
import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.LiquibaseService;
import de.iew.stagediver.fx.database.services.impl.ConnectionServiceImpl;
import de.iew.stagediver.fx.database.services.impl.LiquibaseServiceImpl;
import de.qaware.sdfx.nonosgi.PlatformModule;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Implements a Stagediver FX platform module to use the database framework in non OSGi applications.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 22:07
 */
public class DatabasePlatformModule extends PlatformModule {

    private final Dictionary<String, ?> dbProviderConfiguration;

    public DatabasePlatformModule() {
        this(new Hashtable<String, Object>());
    }

    public DatabasePlatformModule(final Dictionary<String, ?> dbProviderConfiguration) {
        this.dbProviderConfiguration = dbProviderConfiguration;
    }

    @Override
    protected void configure() {
        super.configure();

        final PlatformDBProviderFactory platformDBProviderFactory = new PlatformDBProviderFactory(this.dbProviderConfiguration);

        bind(DBProviderFactory.class).toInstance(platformDBProviderFactory);
        bind(ConnectionService.class).to(ConnectionServiceImpl.class);
        bind(LiquibaseService.class).to(LiquibaseServiceImpl.class);
    }
}
