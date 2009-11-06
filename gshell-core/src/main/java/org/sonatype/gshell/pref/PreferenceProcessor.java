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

import org.sonatype.gshell.util.setter.Setter;
import org.sonatype.gshell.util.setter.SetterFactory;

import java.lang.reflect.AnnotatedElement;
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
public class PreferenceProcessor
{
    private final List<PreferenceDescriptor> descriptors = new ArrayList<PreferenceDescriptor>();

    public PreferenceProcessor() {}

    public PreferenceProcessor(final Object bean) {
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
            for (Method method : type.getDeclaredMethods()) {
                discoverDescriptor(bean, method);
            }
            for (Field field : type.getDeclaredFields()) {
                discoverDescriptor(bean, field);
            }
        }
    }

    private void discoverDescriptor(final Object bean, final AnnotatedElement element) {
        assert bean != null;
        assert element != null;

        Preference pref = element.getAnnotation(Preference.class);
        if (pref != null) {
            addPreference(pref, SetterFactory.create(element, bean));
        }
    }

    private void addPreference(final Preference preference, final Setter setter) {
        descriptors.add(new PreferenceDescriptor(preference, setter));
    }

    public void process() throws Exception {
        for (PreferenceDescriptor desc : descriptors) {
            desc.set();
        }
    }
}