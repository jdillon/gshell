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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.planet57.gossip.Log;
import org.slf4j.Logger;

/**
 * In-memory {@link StyleSource}.
 *
 * @since 3.0
 */
public class MemoryStyleSource
  implements StyleSource
{
  private static final Logger log = Log.getLogger(MemoryStyleSource.class);

  private final Map<String,Map<String,String>> styles = new HashMap<>();

  /**
   * Returns group mapping (or creating if missing) for given group-name.
   */
  public Map<String,String> group(final String name) {
    return styles.computeIfAbsent(name, k -> new HashMap<>());
  }

  @Nullable
  @Override
  public String get(final String group, final String name) {
    String result = group(group).get(name);
    log.debug("Get: {}={} -> {}", group, name, result);
    return result;
  }
}
