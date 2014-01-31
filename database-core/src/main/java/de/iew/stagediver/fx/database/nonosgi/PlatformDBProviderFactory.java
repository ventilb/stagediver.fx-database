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

import de.iew.stagediver.fx.database.provider.*;

import java.util.Dictionary;

/**
 * Implements a {@link DBProviderFactory} to use in non OSGi Stagediver FX applications.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 23:14
 */
public class PlatformDBProviderFactory extends AbstractDBProviderFactory {

    private final Dictionary<String, ?> dbProviderConfiguration;

    public PlatformDBProviderFactory(final Dictionary<String, ?> dbProviderConfiguration) {
        this.dbProviderConfiguration = dbProviderConfiguration;
    }

    @Override
    public DBProvider newDBProvider() throws DBProviderFactoryException {
        try {
            return createDBProvider(this.dbProviderConfiguration);
        } catch (DBProviderConfigurationException e) {
            throw new DBProviderFactoryException(e);
        }
    }
}
