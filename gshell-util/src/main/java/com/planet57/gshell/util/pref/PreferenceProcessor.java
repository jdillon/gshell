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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.converter.Converters;
import com.planet57.gshell.util.setter.Setter;
import com.planet57.gshell.util.setter.SetterFactory;
import org.slf4j.Logger;

/**
 * Processes an object for preference annotations.
 *
 * @since 2.0
 */
public class PreferenceProcessor
{
  private static final Logger log = Log.getLogger(PreferenceProcessor.class);

  private final List<PreferenceDescriptor> descriptors = new ArrayList<PreferenceDescriptor>();

  private String basePath;

  public PreferenceProcessor() {
    // empty
  }

  public List<PreferenceDescriptor> getDescriptors() {
    return descriptors;
  }

  public void addBean(final Object bean) {
    discoverDescriptors(bean);
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(final String path) {
    this.basePath = path;
  }

  //
  // Discovery
  //

  private void discoverDescriptors(final Object bean) {
    assert bean != null;

    // Recursively process all the methods/fields (@Inherited won't work here)
    for (Class<?> type = bean.getClass(); type != null; type = type.getSuperclass()) {
      Preferences base = type.getAnnotation(Preferences.class);

      for (Method method : type.getDeclaredMethods()) {
        discoverDescriptor(base, bean, method);
      }
      for (Field field : type.getDeclaredFields()) {
        discoverDescriptor(base, bean, field);
      }
    }
  }

  private void discoverDescriptor(final Preferences base, final Object bean, final AnnotatedElement element) {
    // base could be null
    assert bean != null;
    assert element != null;

    Preference pref = element.getAnnotation(Preference.class);
    if (pref != null) {
      log.trace("Discovered preference for: {}", element);
      PreferenceDescriptor desc = new PreferenceDescriptor(base, pref, SetterFactory.create(element, bean));
      desc.setBasePath(getBasePath());
      descriptors.add(desc);
    }
  }

  //
  // Processing
  //

  public void process() throws Exception {
    log.trace("Processing preference descriptors");

    for (PreferenceDescriptor desc : descriptors) {
      log.trace("Descriptor: {}", desc);

      java.util.prefs.Preferences prefs = desc.getPreferences();
      log.trace("Using preferences: {}", prefs);

      String key = desc.getId();
      String value = prefs.get(key, null);
      log.trace("  {}={}", key, value);

      if (value != null) {
        Setter setter = desc.getSetter();
        setter.set(Converters.getValue(setter.getType(), value));
      }
    }
  }
}
