package com.planet57.gshell.guice;

import com.google.inject.Injector;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.MutableBeanLocator;

import java.lang.annotation.Annotation;

/**
 * ???
 *
 * @since 3.0
 */
public class BeanContainer
{
  // using MutableBeanLocator as the api here, as impl strips out generics
  private final MutableBeanLocator beanLocator = new DefaultBeanLocator();

  public <Q extends Annotation, T> Iterable<? extends BeanEntry<Q, T>> locate(final Key<T> key) {
    return beanLocator.locate(key);
  }

  public <Q extends Annotation, T, W> void watch(final Key<T> key, Mediator<Q, T, W> mediator, final W watcher) {
    beanLocator.watch(key, mediator, watcher);
  }

  // HACK: expose minimal MutableBeanLocator api needed

  public void add(final Injector injector, final int rank) {
    beanLocator.add(injector, rank);
  }

  public void clear() {
    beanLocator.clear();
  }
}
