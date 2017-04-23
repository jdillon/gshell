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
package com.planet57.gshell.execute;

import com.planet57.gshell.util.Notification;

/**
 * Thrown to indicate that the current shell should exit.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public final class ExitNotification
    extends Notification
{
  private static final long serialVersionUID = 1;

  public static final int SUCCESS_CODE = 0;

  public static final int ERROR_CODE = 1;

  public static final int FATAL_CODE = 2;

  public final int code;

  public ExitNotification(final int code) {
    this.code = code;
  }

  public ExitNotification() {
    this(SUCCESS_CODE);
  }

  public static void exit(final int code) {
    throw new ExitNotification(code);
  }

  public static void exit() {
    throw new ExitNotification();
  }
}
