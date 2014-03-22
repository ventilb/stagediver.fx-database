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

package de.iew.stagediver.fx.database.liquibase.impl

import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import liquibase.parser.core.xml.XMLChangeLogSAXParser

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is as h_is
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue

/**
 * Implements unit tests to observe the liquibase behaviour when it must deal with uris with and without a leading
 * path separator. The problem is, the OSGi and Non-OSGi world has a different approach. OSGi uses URLs to resolve the
 * resources while the Non-OSGi world directly references the DB-changelog file.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 22.03.14 - 10:08
 */
class ClasspathResourceAccessorImplTest extends GroovyTestCase {

    /**
     * Testet, dass die {@link ClasspathResourceAccessorImpl#getResourceAsStream(java.lang.String)}-Methode unabhängig
     * vom führenden Pfadseparator die DB-changelog Datei auflösen kann.
     */
    void testFindDBChangelogFile() {
        // Testfix erstellen
        def dbChangelog1 = "/db-changelog.xml"
        def dbChangelog2 = "db-changelog.xml"
        def dbChangelog3 = null

        // Test durchführen
        def dbChangelogStream1 = testee.getResourceAsStream(dbChangelog1)
        def dbChangelogStream2 = testee.getResourceAsStream(dbChangelog2)
        def dbChangelogStream3 = testee.getResourceAsStream(dbChangelog3)

        // Test auswerten
        assertThat(dbChangelogStream1, h_is(notNullValue()))
        assertThat(dbChangelogStream2, h_is(notNullValue()))
        assertThat(dbChangelogStream3, h_is(nullValue()))
    }

    /**
     * Testet, dass der Liquibase Parsevorgang der DB-changelog Datei mit führenden Pfadseparatoren funktioniert.
     */
    void testParseDBChangelogFileShouldNotFail_with_leading_path_separator() {
        // Testfix erstellen
        def dbChangelog = "/db-changelog.xml"
        def changeLogParameters = new ChangeLogParameters()

        // Test durchführen
        def XMLChangeLogSAXParser changeLogSAXParser = new XMLChangeLogSAXParser()
        def DatabaseChangeLog databaseChangeLog = changeLogSAXParser.parse(dbChangelog, changeLogParameters, testee)

        // Test auswerten
        assertThat(databaseChangeLog.changeSets, hasSize(11))
    }

    /**
     * Testet, dass der Liquibase Parsevorgang der DB-changelog Datei ohne führenden Pfadseparatoren funktioniert.
     */
    void testParseDBChangelogFileShouldNotFail_without_leading_path_separator() {
        // Testfix erstellen
        def dbChangelog = "db-changelog.xml"
        def changeLogParameters = new ChangeLogParameters()

        // Test durchführen
        def XMLChangeLogSAXParser changeLogSAXParser = new XMLChangeLogSAXParser()
        def DatabaseChangeLog databaseChangeLog = changeLogSAXParser.parse(dbChangelog, changeLogParameters, testee)

        // Test auswerten
        assertThat(databaseChangeLog.changeSets, hasSize(11))
    }

    @Override
    void setUp() {
        super.setUp();
        testee = new ClasspathResourceAccessorImpl()
    }

    def ClasspathResourceAccessorImpl testee
}
