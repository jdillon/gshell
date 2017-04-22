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
      holder.set(terminal);
    }

    return last;
  }

  @Nullable
  public static Terminal get(final boolean allowNull) {
    Terminal Terminal = holder.get();

    if (!allowNull && Terminal == null) {
      throw new IllegalStateException("Terminal not initialized for thread: " + Thread.currentThread());
    }

    return Terminal;
  }

  @Nonnull
  public static Terminal get() {
    //noinspection ConstantConditions
    return get(false);
  }
}
