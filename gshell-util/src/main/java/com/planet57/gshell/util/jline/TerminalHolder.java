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
package com.planet57.gshell.util.jline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link Terminal} thread context holder.
 *
 * @since 3.0
 */
public class TerminalHolder
{
  private static final Logger log = LoggerFactory.getLogger(TerminalHolder.class);

  private static final InheritableThreadLocal<Terminal> holder = new InheritableThreadLocal<>();

  public static Terminal set(@Nullable final Terminal terminal) {
    Terminal last = holder.get();

    if (terminal != last) {
      log.trace("Setting terminal: {}", terminal);
      if (terminal == null) {
        holder.remove();
      }
      else {
        holder.set(terminal);
      }
    }

    return last;
  }

  @Nullable
  public static Terminal get() {
    return holder.get();
  }

  @Nonnull
  public static Terminal require() {
    Terminal terminal = get();
    checkState(terminal != null, "Terminal not initialized for thread: %s", Thread.currentThread());
    return terminal;
  }
}
