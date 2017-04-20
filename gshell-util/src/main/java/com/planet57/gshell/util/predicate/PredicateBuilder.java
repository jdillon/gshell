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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * {@link Predicate} builder.
 *
 * @since 3.0
 */
public class PredicateBuilder<T>
{
  private final List<Predicate<T>> predicates = new ArrayList<>();

  public PredicateBuilder<T> include(final Predicate<T>... predicates) {
    this.predicates.addAll(Arrays.asList(predicates));
    return this;
  }

  public PredicateBuilder<T> and(final Predicate<T> predicate) {
    predicates.add(Predicates.and(predicate));
    return this;
  }

  public PredicateBuilder<T> not(final Predicate<T> predicate) {
    predicates.add(Predicates.not(predicate));
    return this;
  }

  public Predicate<T> build() {
    return Predicates.and(predicates);
  }
}
