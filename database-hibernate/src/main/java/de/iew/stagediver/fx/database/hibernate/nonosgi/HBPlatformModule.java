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

package de.iew.stagediver.fx.database.hibernate.nonosgi;

import com.google.inject.Provides;
import de.qaware.sdfx.nonosgi.PlatformModule;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Implements a stagediver.fx platform module to bootstrap hibernate in non OSGi applications.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 31.01.14 - 22:29
 */
public class HBPlatformModule extends PlatformModule {

    private SessionFactory sessionFactory;

    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    public SessionFactory createSessionFactory() {
        if (this.sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
            serviceRegistryBuilder.applySettings(configuration.getProperties());

            StandardServiceRegistry serviceRegistry = serviceRegistryBuilder.build();

            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return this.sessionFactory;
    }
}
