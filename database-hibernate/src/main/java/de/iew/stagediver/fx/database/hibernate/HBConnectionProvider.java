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

package de.iew.stagediver.fx.database.hibernate;

import de.iew.stagediver.fx.database.services.ConnectionService;
import de.iew.stagediver.fx.database.services.exception.ConnectException;
import de.iew.stagediver.fx.database.services.exception.ConnectionReleaseException;
import de.qaware.sdfx.lookup.Lookup;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Configurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Implements a hibernate connection provider to act as a bridge between our own {@link ConnectionService} and
 * hibernate.
 * <p>
 * Currently it requires to configure database, username and password. In the future it's planned to only work with
 * database profiles which are configured in the underlying connection service framework.
 * </p>
 * <p>
 * To use this connection provider you must use the following properties in your {@code hibernate.cfg.xml}.
 * </p>
 * <pre>
 * {@code
 * <property name="connection.provider_class">de.iew.stagediver.fx.database.hibernate.HBConnectionProvider</property>
 * <property name="de.iew.stagediver.fx.database.hibernate.database">example_db</property>
 * <property name="de.iew.stagediver.fx.database.hibernate.username">foo</property>
 * <property name="de.iew.stagediver.fx.database.hibernate.password">bar</property>
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @see <a href="http://jeff.familyyu.net/2011/09/08/upgrading-hibernate-3-3-2-to-hibernate-4-0-0/">http://jeff.familyyu.net/2011/09/08/upgrading-hibernate-3-3-2-to-hibernate-4-0-0/</a>
 * @since 19.01.14 - 15:56
 */
public class HBConnectionProvider implements ConnectionProvider, Configurable {

    private static Logger log = LoggerFactory.getLogger(HBConnectionProvider.class);

    private static final Lookup LOOKUP = new Lookup(HBConnectionProvider.class);

    private static final String PREFIX = "de.iew.stagediver.fx.database.hibernate.";

    public static final String IEW_DATABASE = PREFIX + "database";

    public static final String IEW_DATABASE_USERNAME = PREFIX + "username";

    public static final String IEW_DATABASE_PASSWORD = PREFIX + "password";

    private String database;

    private String username;

    private String password;

    @Override
    public void configure(Map configurationValues) {
        log.debug("HBConnectionProvider.configure({})", configurationValues);

        this.database = (String) configurationValues.get(IEW_DATABASE);
        this.username = (String) configurationValues.get(IEW_DATABASE_USERNAME);
        this.password = (String) configurationValues.get(IEW_DATABASE_PASSWORD);

        verify();
    }

    protected void verify() {
        final boolean databaseOk = StringUtils.isNotBlank(this.database);

        if (!(databaseOk)) {
            throw new HibernateException("Configuration error: database name is required");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            final ConnectionService connectionService = LOOKUP.lookup(ConnectionService.class);
            return connectionService.checkoutBuildInDatabaseConnection(this.database, this.username, this.password);
        } catch (ConnectException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        log.debug("HBConnectionProvider.closeConnection({})", conn);
        final ConnectionService connectionService = LOOKUP.lookup(ConnectionService.class);
        try {
            connectionService.releaseConnection(conn);
        } catch (ConnectionReleaseException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

}
