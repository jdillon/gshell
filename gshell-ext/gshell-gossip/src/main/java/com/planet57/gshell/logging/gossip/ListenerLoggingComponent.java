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
package com.planet57.gshell.logging.gossip;

import com.planet57.gossip.listener.Listener;
import com.planet57.gshell.logging.LoggingComponent;
import com.planet57.gshell.logging.LoggingComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link LoggingComponent} for {@link Listener}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class ListenerLoggingComponent
    extends LoggingComponentSupport
{
  private final Listener listener;

  public ListenerLoggingComponent(final Listener listener) {
    super(Listener.class.getName());
    this.listener = checkNotNull(listener);
  }

  public Listener getListener() {
    return listener;
  }

  @Override
  public Object getTarget() {
    return getListener();
  }
}