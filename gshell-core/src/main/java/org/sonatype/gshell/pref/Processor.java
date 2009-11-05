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

import org.sonatype.gshell.util.setter.MethodSetter;
import org.sonatype.gshell.util.setter.Setter;
import org.sonatype.gshell.util.setter.SetterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes an object for preference annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class Processor
{
    private final List<PreferenceDescriptor> descriptors = new ArrayList<PreferenceDescriptor>();

    public Processor() {}

    public Processor(final Object bean) {
        addBean(bean);
    }

    public void addBean(final Object bean) {
        discoverDescriptors(bean);
    }

    //
    // Discovery
    //

    private void discoverDescriptors(final Object bean) {
        assert bean != null;

        // Recursively process all the methods/fields (@Inherited won't work here)
        for (Class type=bean.getClass(); type!=null; type=type.getSuperclass()) {
            // Discover methods
            for (Method method : type.getDeclaredMethods()) {
                Preference option = method.getAnnotation(Preference.class);
                if (option != null) {
                    addPreference(new MethodSetter(bean, method), option);
                }
            }

            // Discover fields
            for (Field field : type.getDeclaredFields()) {
                Preference option = field.getAnnotation(Preference.class);
                if (option != null) {
                    addPreference(SetterFactory.create(bean, field), option);
                }
            }
        }
    }

    private void addPreference(final Setter setter, final Preference preference) {
        descriptors.add(new PreferenceDescriptor(setter, preference));
    }

    public void process() throws Exception {
        for (PreferenceDescriptor desc : descriptors) {
            desc.set();
        }
    }
}