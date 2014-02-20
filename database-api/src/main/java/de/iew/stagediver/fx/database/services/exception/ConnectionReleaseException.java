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
 * Implements an exception to indicate if a connection could not be released.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 20.02.14 - 20:03
 */
public class ConnectionReleaseException extends Exception {
    public ConnectionReleaseException() {
    }

    public ConnectionReleaseException(String message) {
        super(message);
    }

    public ConnectionReleaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionReleaseException(Throwable cause) {
        super(cause);
    }
}
