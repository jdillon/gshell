/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet57.gshell.util.i18n;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.planet57.gossip.Log;
import org.slf4j.Logger;

/**
 * Message source backed up by {@link ResourceBundle} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ResourceBundleMessageSource
    implements MessageSource
{
    private static final Logger log = Log.getLogger(ResourceBundleMessageSource.class);

    private final List<ResourceBundle> bundles = new LinkedList<ResourceBundle>();

    private final Locale locale;

    public ResourceBundleMessageSource(final Locale locale) {
        assert locale != null;
        this.locale = locale;
    }

    public ResourceBundleMessageSource(final Class... types) {
        this(Locale.getDefault());
        add(types);
    }

    public ResourceBundleMessageSource add(final boolean required, final Class... types) {
        assert types != null;

        for (Class type : types) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(type.getName(), locale, type.getClassLoader());
                bundles.add(bundle);
            }
            catch (MissingResourceException e) {
                if (required) {
                    throw e;
                }
            }
        }

        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public ResourceBundleMessageSource add(final Class... types) {
        return add(true, types);
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
            catch (MissingResourceException e) {
                log.trace(e.toString(), e);
            }
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
