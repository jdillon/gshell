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
package com.planet57.gshell.internal;

import com.google.inject.Injector;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.MutableBeanLocator;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice container abstraction.
 *
 * @since 3.0
 */
public class BeanContainer
{
  // using MutableBeanLocator as the api here, as impl strips out generics
  private final MutableBeanLocator beanLocator;

  public BeanContainer(final MutableBeanLocator beanLocator) {
    this.beanLocator = checkNotNull(beanLocator);
  }

  public BeanContainer() {
    this(new DefaultBeanLocator());
  }

  public MutableBeanLocator getBeanLocator() {
    return beanLocator;
  }

  public <Q extends Annotation, T> Iterable<? extends BeanEntry<Q, T>> locate(final Key<T> key) {
    checkNotNull(key);
    return beanLocator.locate(key);
  }

  public <Q extends Annotation, T> Iterable<? extends BeanEntry<Q, T>> locate(final Class<T> type) {
    checkNotNull(type);
    return beanLocator.locate(Key.get(type));
  }

  public <Q extends Annotation, T, W> void watch(final Key<T> key, Mediator<Q, T, W> mediator, final W watcher) {
    beanLocator.watch(key, mediator, watcher);
  }

  public void add(final Injector injector, final int rank) {
    beanLocator.add(injector, rank);
  }

  public void clear() {
    beanLocator.clear();
  }
}
