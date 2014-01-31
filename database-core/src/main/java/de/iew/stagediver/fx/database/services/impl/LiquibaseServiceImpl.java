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

package de.iew.stagediver.fx.database.services.impl;

import de.iew.stagediver.fx.database.liquibase.ResourceAccessor;
import de.iew.stagediver.fx.database.liquibase.impl.BundleResourceAccessorImpl;
import de.iew.stagediver.fx.database.liquibase.impl.ClasspathResourceAccessorImpl;
import de.iew.stagediver.fx.database.services.LiquibaseService;
import de.iew.stagediver.fx.database.services.exception.LiquibaseServiceException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import org.osgi.framework.BundleContext;

import java.sql.Connection;

/**
 * OSGi implementation of a liquibase service.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.01.14 - 22:51
 */
public class LiquibaseServiceImpl implements LiquibaseService {

    // Geht leider mit Liquibase nicht. Wir müssen den ResourceAccessor bei jedem Methodenaufruf mitschleifen, da
    // Liquibase nur mit Strings umgehen kann.
//    private ResourceAccessor liquibaseResourceAccessor;
//
//    protected void activate(ComponentContext componentContext) {
//        setLiquibaseResourceAccessor(new LiquibaseOSGIResourceAccessor(componentContext.getBundleContext()));
//    }

    @Override
    public void runLiquibase(final String changelog, ResourceAccessor resourceAccessor, final Connection connection, String... liquibaseContexts) throws LiquibaseServiceException {
        try {
            final Database liquibaseDatabase = newLiquibaseDatabase(connection);

            final liquibase.resource.ResourceAccessor liquibaseResourceAccessor = resolveAccessor(resourceAccessor);
            final Liquibase liquibase = newLiquibase(changelog, liquibaseResourceAccessor, liquibaseDatabase);

            // TODO Prüfen ob man Liquibase nicht refactoren kann und von der Changlog eingabe Ressource abstrahieren könnte damit der resourceAccessor Parameter entfällt

            liquibase.update(null);
        } catch (LiquibaseException e) {
            throw new LiquibaseServiceException(e);
        }
    }

    @Override
    public ResourceAccessor newBundleResourceAccessor(BundleContext context) {
        return new BundleResourceAccessorImpl(context);
    }

    @Override
    public ResourceAccessor newClasspathResourceAccessor() {
        return new ClasspathResourceAccessorImpl();
    }

    public liquibase.resource.ResourceAccessor resolveAccessor(ResourceAccessor resourceAccessor) throws LiquibaseServiceException {
        if (resourceAccessor instanceof liquibase.resource.ResourceAccessor) {
            return (liquibase.resource.ResourceAccessor) resourceAccessor;
        }
        throw new LiquibaseServiceException("Cannot resolve resource accessor");
    }

    public Liquibase newLiquibase(final String changelog, liquibase.resource.ResourceAccessor resourceAccessor, final Database liquibaseDatabaseInstance) throws LiquibaseException {
        final Liquibase liquibase = new Liquibase(changelog, resourceAccessor, liquibaseDatabaseInstance);
        return liquibase;
    }

    public Database newLiquibaseDatabase(Connection connection) throws LiquibaseException {
        final DatabaseFactory databaseFactory = getLiquibaseDatabaseFactory();
        final JdbcConnection jdbcConnection = new JdbcConnection(connection);

        return databaseFactory.findCorrectDatabaseImplementation(jdbcConnection);
    }

    public DatabaseFactory getLiquibaseDatabaseFactory() {
        return DatabaseFactory.getInstance();
    }

}
