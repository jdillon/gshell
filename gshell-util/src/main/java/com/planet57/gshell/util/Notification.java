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
package com.planet57.gshell.util;

// TODO: could normalize with goodies-common, except it lacks a cause which is needed by ErrorNotification

/**
 * Thrown to indicate a notification state.
 * <p/>
 * <p>Extending from Error to prevent need to declare throwable.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class Notification
    extends Error
{
  public Notification(final String msg, final Throwable cause) {
    super(msg, cause);
  }

  public Notification(final String msg) {
    super(msg);
  }

  public Notification(final Throwable cause) {
    super(cause);
  }

  public Notification() {
    super();
  }
}
