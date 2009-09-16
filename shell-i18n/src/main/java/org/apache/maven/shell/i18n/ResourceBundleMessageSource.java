/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message source backed up by one or more {@link ResourceBundle} instances.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResourceBundleMessageSource
    implements MessageSource
{
    private final List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();

    private final Locale locale;

    public ResourceBundleMessageSource(final Class[] types) {
        assert types != null;

        locale = Locale.getDefault();

        for (Class type : types) {
            loadBundle(type);
        }
    }

    public ResourceBundleMessageSource(final Class type) {
        this(new Class[] { type });
    }

    private void loadBundle(final Class type) {
        assert type != null;

        ResourceBundle bundle = ResourceBundle.getBundle(type.getName(), locale, type.getClassLoader());
        bundles.add(bundle);
    }

    /**
     * Get a raw message from the resource bundles using the given code.
     */
    public String getMessage(final String code) {
        assert code != null;

        for (ResourceBundle bundle : bundles) {
            try {
                return bundle.getString(code);
            }
            catch (MissingResourceException ignore) {}
        }

        throw new ResourceNotFoundException(code);
    }

    /**
     * Format a message (based on {@link MessageFormat} using the message
     * from the resource bundles using the given code as a pattern and the
     * given objects as arguments.
     */
    public String format(final String code, final Object... args) {
        // args may be null

        String pattern = getMessage(code);
        if (args != null) {
            return String.format(pattern, args);
        }
        else {
            return pattern;
        }
    }
}
