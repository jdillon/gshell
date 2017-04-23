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
package com.planet57.gshell.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link Shell} thread context holder.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ShellHolder
{
  private static final Logger log = LoggerFactory.getLogger(ShellHolder.class);

  private static final InheritableThreadLocal<Shell> holder = new InheritableThreadLocal<>();

  public static Shell set(@Nullable final Shell shell) {
    Shell last = holder.get();

    if (shell != last) {
      log.trace("Setting shell: {}", shell);
      if (shell == null) {
        holder.remove();
      }
      else {
        holder.set(shell);
      }
    }

    return last;
  }

  // FIXME: remove the need for folks to get this via ThreadLocal

  /**
   * @since 3.0
   */
  @Nonnull
  public static Shell require() {
    Shell shell = holder.get();
    checkState(shell != null, "Shell not initialized for thread: %s", Thread.currentThread());
    return shell;
  }
}
