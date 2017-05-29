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
package com.planet57.gshell.util.style;

import com.planet57.gossip.Log;
import com.planet57.gshell.util.style.StyleBundle.StyleGroup;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Style facade.
 *
 * @since 3.0
 * @see StyleBundle
 * @see StyleFactory
 * @see StyleSource
 */
public class Styler
{
  private static final Logger log = Log.getLogger(Styler.class);

  private static volatile StyleSource source = new NopStyleSource();

  private Styler() {
    // empty
  }

  /**
   * Install global {@link StyleSource}.
   */
  public static void setSource(final StyleSource source) {
    Styler.source = checkNotNull(source);
    log.debug("Source: {}", source);
  }

  /**
   * Returns global {@link StyleSource}.
   */
  public static StyleSource getSource() {
    return source;
  }

  /**
   * Create a resolver for the given style-group.
   */
  public static StyleResolver resolver(final String group) {
    return new StyleResolver(source, group);
  }

  /**
   * Create a factory for the given style-group.
   */
  public static StyleFactory factory(final String group) {
    return new StyleFactory(resolver(group));
  }

  /**
   * Create a {@link StyleBundle} proxy.
   *
   * Target class must be annotated with {@link StyleGroup}.
   */
  public static < T extends StyleBundle> T bundle(final Class<T> type) {
    return StyleBundleInvocationHandler.create(source, type);
  }

  /**
   * Create a {@link StyleBundle} proxy with explicit style-group.
   */
  public static < T extends StyleBundle> T bundle(final String group, final Class<T> type) {
    return StyleBundleInvocationHandler.create(resolver(group), type);
  }
}
