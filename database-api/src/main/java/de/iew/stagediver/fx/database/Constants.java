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

package de.iew.stagediver.fx.database;

import de.iew.stagediver.fx.database.services.ConnectionService;

/**
 * The public constants required to access and configure certain levels of the database framework.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 22:25
 */
public interface Constants {

    /**
     * The key used to configure the db provider class.
     */
    public static final String DB_PROVIDER_CLASS = ConnectionService.class.getName() + ".DB_PROVIDER_CLASS";

}
