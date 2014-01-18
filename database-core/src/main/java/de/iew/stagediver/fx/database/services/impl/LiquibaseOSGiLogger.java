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

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.logging.Logger;
import liquibase.logging.core.AbstractLogger;
import org.slf4j.LoggerFactory;

/**
 * Implements the liquibase {@link liquibase.logging.Logger} to create a bridge between liquibase log API and slf4j.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 17.01.14 - 20:50
 */
public class LiquibaseOSGiLogger extends AbstractLogger implements Logger {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(LiquibaseOSGiLogger.class);

    private int priority = 1;

    private String name;

    private DatabaseChangeLog databaseChangeLog;

    private ChangeSet changeSet;

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setLogLevel(String logLevel, String logFile) {
        setLogLevel(logLevel);
    }

    @Override
    public void severe(String message) {
        log.error(message);
    }

    @Override
    public void severe(String message, Throwable e) {
        log.error(message, e);
    }

    @Override
    public void warning(String message) {
        log.warn(message);
    }

    @Override
    public void warning(String message, Throwable e) {
        log.warn(message, e);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Throwable e) {
        log.info(message, e);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    @Override
    public void debug(String message, Throwable e) {
        log.debug(message, e);
    }

    @Override
    public void setChangeLog(DatabaseChangeLog databaseChangeLog) {
        this.databaseChangeLog = databaseChangeLog;
    }

    @Override
    public void setChangeSet(ChangeSet changeSet) {
        this.changeSet = changeSet;
    }
}
