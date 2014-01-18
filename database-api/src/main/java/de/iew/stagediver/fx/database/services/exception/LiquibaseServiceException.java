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

package de.iew.stagediver.fx.database.services.exception;

/**
 * Exception to indicate errors in the {@link de.iew.stagediver.fx.database.services.LiquibaseService} which prevent
 * the service to succeed the operation.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 16.01.14 - 23:35
 */
public class LiquibaseServiceException extends Exception {
    public LiquibaseServiceException(String message) {
        super(message);
    }

    public LiquibaseServiceException(Throwable cause) {
        super(cause);
    }
}
