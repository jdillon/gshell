/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.util.pref;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.util.converter.Converters;
import org.sonatype.gshell.util.setter.Setter;

import java.util.prefs.Preferences;

/**
 * Descriptor for {@link Preference} annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class PreferenceDescriptor
{
    private static final Logger log = Log.getLogger(PreferenceDescriptor.class);

    private final Preference spec;

    private final Setter setter;

    private final String name;

    private final Class<?> base;

    private final boolean system;

    protected PreferenceDescriptor(final Preference pref, final Setter setter) {
        assert pref != null;
        this.spec = pref;
        assert setter != null;
        this.setter = setter;

        // Handle "" = null, since default values in annotations cannot be set to null
        if (pref.name() != null && pref.name().length() == 0) {
            this.name = null;
        }
        else {
            this.name = pref.name();
        }

        this.system = pref.system();

        // On IBM JDK, the value passed is null instead of the default value, so fix it in case
        this.base = pref.base() != null ? pref.base() : Void.class;
    }

    public Preference getSpec() {
        return spec;
    }

    public Setter getSetter() {
        return setter;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBase() {
        return base;
    }

    public boolean isSystem() {
        return system;
    }

    public String getId() {
        if (name != null) {
            return name;
        }
        return getSetter().getName();
    }

    public Class getType() {
        Class type = base;
        if (type == Void.class) {
            type = getSetter().getBean().getClass();
        }
        return type;
    }

    public Preferences getPreferences() {
        Class type = getType();

        if (system) {
            return Preferences.systemNodeForPackage(type);
        }
        else {
            return Preferences.userNodeForPackage(type);
        }
    }

    public void set() throws Exception {
        Preferences prefs = getPreferences();
        log.debug("Using preferences: {}", prefs);

        String key = getId();
        String value = prefs.get(key, null);
        log.debug("  {}={}", key, value);
        
        if (value != null) {
            setter.set(Converters.getValue(setter.getType(), value));
        }
    }
}