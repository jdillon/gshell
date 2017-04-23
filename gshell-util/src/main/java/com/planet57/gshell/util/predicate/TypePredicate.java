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
package com.planet57.gshell.util.predicate;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate to include objects assignable from a given type.
 *
 * @since 3.0
 */
public class TypePredicate<T>
    implements Predicate<T>
{
  private final Class<?> type;

  public TypePredicate(final Class<?> type) {
    this.type = checkNotNull(type);
  }

  @Override
  public boolean test(final T value) {
    return value != null && type.isAssignableFrom(value.getClass());
  }

  public static <T> Predicate<T> of(final Class<?> type) {
    return new TypePredicate<>(type);
  }
}
