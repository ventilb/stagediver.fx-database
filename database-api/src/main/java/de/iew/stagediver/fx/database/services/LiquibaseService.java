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

package de.iew.stagediver.fx.database.services;

import de.iew.stagediver.fx.database.liquibase.ResourceAccessor;
import de.iew.stagediver.fx.database.services.exception.LiquibaseServiceException;
import org.osgi.framework.BundleContext;

import java.sql.Connection;

/**
 * Specifies an interface to implement services to execute liquibase changelog files against databases.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @see <a href="http://www.liquibase.org/">http://www.liquibase.org/</a>
 * @since 16.01.14 - 22:45
 */
public interface LiquibaseService {

    /**
     * Applies the liquibase changelog to the database identified by the given connection.
     *
     * @param changelog         the liquibase changelog
     * @param resourceAccessor  the liquibase resource accessor to lookup the changelog files in the calling bundle
     * @param connection        the database connection to use
     * @param liquibaseContexts list of liquibase contexts to apply (can be empty)
     * @throws LiquibaseServiceException in case an error prevented the service call to
     *                                   succeed
     */
    public void runLiquibase(String changelog, ResourceAccessor resourceAccessor, Connection connection, String... liquibaseContexts) throws LiquibaseServiceException;

    /**
     * Returns a new {@link de.iew.stagediver.fx.database.liquibase.ResourceAccessor} instance for the specified OSGi
     * bundle.
     * <p>
     * The resource accessor is required to access changelog files from different bundles.
     * </p>
     *
     * @param context the osgi bundle context
     * @return the resource accessor instance
     */
    public ResourceAccessor newBundleResourceAccessor(BundleContext context);
}
