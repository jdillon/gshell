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
package com.planet57.gshell.command.descriptor;

import java.util.EventObject;

/**
 * Event fired once a {@link CommandDescriptor} has been discovered.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class DiscoveredCommandDescriptorEvent
    extends EventObject
{
  ///CLOVER:OFF

  private CommandDescriptor descriptor;

  public DiscoveredCommandDescriptorEvent(final CommandDescriptor descriptor) {
    super(descriptor);

    assert descriptor != null;
    this.descriptor = descriptor;
  }

  public CommandDescriptor getDescriptor() {
    return descriptor;
  }
}