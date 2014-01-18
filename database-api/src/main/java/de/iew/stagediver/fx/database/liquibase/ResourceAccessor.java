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

package de.iew.stagediver.fx.database.liquibase;

/**
 * Specifies an interface to implement resource accessor for the liquibase service. This interface is meant as an
 * abstraction to liquibase's own resource accessor api. I had trouble hiding liquibase in an OSGi environment because
 * it requires a resource accessor to function. And i didn't managed to provide liquibase's own resource accessor api
 * in OSGi.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 18.01.14 - 19:18
 */
public interface ResourceAccessor {
}
