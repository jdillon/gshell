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

package org.sonatype.gshell.pref;

import org.sonatype.gshell.util.converter.Converters;
import org.sonatype.gshell.util.setter.Setter;

import java.util.prefs.Preferences;

/**
 * Descriptor for {@link Preference} annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class PreferenceDescriptor
{
    private final Setter setter;

    private final String name;

    private final Class<?> base;

    private final boolean system;

    protected PreferenceDescriptor(final Setter setter, final Preference pref) {
        assert setter != null;
        this.setter = setter;
        assert pref != null;

        // Handle "" = null, since default values in annotations can be set to null
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

    public void set() throws Exception {
        Preferences root = getRoot();
        Preferences node = root.node(name);
        setter.set(Converters.getValue(setter.getType(), node.get(name, null)));
    }

    private Preferences getRoot() {
        Preferences root;
        if (base != Void.class) {
            if (system) {
                root = Preferences.systemNodeForPackage(base);
            }
            else {
                root = Preferences.userNodeForPackage(base);
            }
        }
        else {
            if (system) {
                root = Preferences.systemRoot();
            }
            else {
                root = Preferences.userRoot();
            }
        }
        return root;
    }
}