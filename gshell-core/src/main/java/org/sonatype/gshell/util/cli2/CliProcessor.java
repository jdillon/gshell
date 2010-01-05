/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.util.cli2;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.util.IllegalAnnotationError;
import org.sonatype.gshell.util.pref.Preference;
import org.sonatype.gshell.util.pref.PreferenceDescriptor;
import org.sonatype.gshell.util.pref.Preferences;
import org.sonatype.gshell.util.setter.SetterFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes an object for cli annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class CliProcessor
{
    private static final Logger log = Log.getLogger(CliProcessor.class);

    private final List<OptionDescriptor> optionDescriptors = new ArrayList<OptionDescriptor>();

    private final List<ArgumentDescriptor> argumentDescriptors = new ArrayList<ArgumentDescriptor>();

    public CliProcessor() {
    }

    public List<OptionDescriptor> getOptionDescriptors() {
        return optionDescriptors;
    }

    public List<ArgumentDescriptor> getArgumentDescriptors() {
        return argumentDescriptors;
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
        for (Class<?> type = bean.getClass(); type != null; type = type.getSuperclass()) {
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

        Option opt = element.getAnnotation(Option.class);
        Argument arg = element.getAnnotation(Argument.class);

        if (opt != null && arg != null) {
            throw new IllegalAnnotationError("Element can only implement @Option or @Argument, not both: " + element);
        }

        if (opt != null) {
            log.trace("Discovered option for: {}", element);
            optionDescriptors.add(new OptionDescriptor(opt, SetterFactory.create(element, bean)));
        }
        else {
            log.trace("Discovered argument for: {}", element);
            argumentDescriptors.add(new ArgumentDescriptor(arg, SetterFactory.create(element, bean)));
        }
    }

    //
    // Processing
    //

    public void process() throws Exception {
        // TODO:
    }
}