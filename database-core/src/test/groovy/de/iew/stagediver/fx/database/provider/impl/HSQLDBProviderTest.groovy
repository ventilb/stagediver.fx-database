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

package de.iew.stagediver.fx.database.provider.impl

import de.iew.stagediver.fx.database.provider.DBProviderConfigurationException
import org.osgi.service.cm.ConfigurationException

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.MatcherAssert.assertThat

/**
 * Unit tests for the {@link HSQLDBProvider} implementation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 19.01.14 - 01:25
 */
class HSQLDBProviderTest extends GroovyTestCase {

    void testConfigureAndVerify_verified() {
        // Testfix aufbauen
        def properties = [
                (HSQLDBProvider.DATABASE_PATH): System.getProperty("java.io.tmpdir"),
                (HSQLDBProvider.DRIVER_CLASS): "org.hsqldb.jdbcDriver"
        ] as Hashtable

        // Test durchführen
        def hsqldbProvider = new HSQLDBProvider()
        hsqldbProvider.configure(properties)

        try {
            hsqldbProvider.verify()
        } catch (DBProviderConfigurationException e) {
            e.printStackTrace()
        }

        // Test auswerten
        assertThat hsqldbProvider.getDatabasePath(), is(System.getProperty("java.io.tmpdir"))
        assertThat hsqldbProvider.getDriverClass(), is("org.hsqldb.jdbcDriver")

    }

    void testConfigureAndVerify_not_verified_invalid_directory() {
        // Testfix aufbauen
        def properties = [
                (HSQLDBProvider.DATABASE_PATH): System.getProperty("java.io.tmpdir") + File.separator + "something_really_went_wrong",
                (HSQLDBProvider.DRIVER_CLASS): "org.hsqldb.jdbcDriver"
        ] as Hashtable

        // Test durchführen und auswerten
        def hsqldbProvider = new HSQLDBProvider()
        hsqldbProvider.configure(properties);

        shouldFail(DBProviderConfigurationException.class, {
            hsqldbProvider.verify()
        })
    }

    void testCreateDatabaseConnectionUrl() {
        // Testfix aufbauen
        def properties = [
                (HSQLDBProvider.DATABASE_PATH): System.getProperty("java.io.tmpdir"),
                (HSQLDBProvider.DRIVER_CLASS): "org.hsqldb.jdbcDriver"
        ] as Hashtable

        // Test durchführen
        def hsqldbProvider = new HSQLDBProvider()
        hsqldbProvider.configure(properties)

        def databaseConnectionUrl = hsqldbProvider.createDatabaseConnectionUrl("test_db")

        // Test auswerten
        assertThat databaseConnectionUrl, is("jdbc:hsqldb:file:" + System.getProperty("java.io.tmpdir") + File.separator + "test_db")
    }
}
