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
package com.planet57.gshell.util.pref;

import com.planet57.gshell.util.AnnotationDescriptor;
import com.planet57.gshell.util.setter.Setter;

/**
 * Descriptor for {@link Preference} annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class PreferenceDescriptor
    extends AnnotationDescriptor
{
    private final Preferences base;

    private final Preference spec;

    private final Setter setter;

    private final String name;

    private final Class<?> type;

    private final boolean system;

    private final String path;

    private String basePath;

    protected PreferenceDescriptor(final Preferences base, final Preference pref, final Setter setter) {
        // base could be null
        this.base = base;
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
        this.type = pref.type() != null ? pref.type() : UNINITIALIZED_CLASS;

        // Handle "" = null, since default values in annotations cannot be set to null
        if (pref.path() != null && pref.path().length() == 0) {
            this.path = null;
        }
        else {
            this.path = pref.path();
        }
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

    public boolean isSystem() {
        return system;
    }

    public String getPath() {
        return path;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(final String path) {
        this.basePath = path;
    }

    public String getId() {
        if (name != null) {
            return name;
        }
        return getSetter().getName();
    }

    public Class getType() {
        Class type = this.type;
        if (type == UNINITIALIZED_CLASS) {
            type = getSetter().getBean().getClass();
        }
        return type;
    }

    public String buildPath() {
        StringBuffer buff = new StringBuffer();

        if (getBasePath() != null) {
            buff.append(getBasePath());
        }

        if (base != null) {
            if (buff.length() != 0) {
                buff.append("/");
            }
            buff.append(base.path());
        }

        if (getPath() != null) {
            if (buff.length() != 0) {
                buff.append("/");
            }
            buff.append(getPath());
        }

        if (buff.length() == 0) {
            return null;
        }
        else {
            return buff.toString();
        }
    }

    public java.util.prefs.Preferences getPreferences() {
        String path = buildPath();

        if (path == null) {
            Class type = getType();

            if (system) {
                return java.util.prefs.Preferences.systemNodeForPackage(type);
            }
            else {
                return java.util.prefs.Preferences.userNodeForPackage(type);
            }
        }
        else {
            if (system) {
                return java.util.prefs.Preferences.systemRoot().node(path);
            }
            else {
                return java.util.prefs.Preferences.userRoot().node(path);
            }
        }
    }
}