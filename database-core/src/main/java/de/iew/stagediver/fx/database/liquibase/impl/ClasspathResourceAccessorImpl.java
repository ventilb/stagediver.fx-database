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

package de.iew.stagediver.fx.database.liquibase.impl;

import de.iew.stagediver.fx.database.liquibase.ResourceAccessor;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a resource accessor to access liquibase resources through the class loader.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 30.01.14 - 23:42
 */
public class ClasspathResourceAccessorImpl extends ClassLoaderResourceAccessor implements ResourceAccessor {

    /**
     * {@inheritDoc}
     * <p>
     * This method is overloaded to strip of an {@code /}-characters from the beginning of {@code file}.
     * </p>
     *
     * @param file the path to the resource
     * @return an input stream to the resource
     * @throws IOException if an io error occured
     */
    @Override
    public InputStream getResourceAsStream(final String file) throws IOException {
        if (file == null) {
            return null;
        }

        final String strippedFilename = StringUtils.stripStart(file, "/");
        return super.getResourceAsStream(strippedFilename);
    }
}
