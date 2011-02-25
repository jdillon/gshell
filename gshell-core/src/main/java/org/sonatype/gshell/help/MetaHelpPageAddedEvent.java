/**
 * Copyright (c) 2009-2011 the original author or authors.
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
package org.sonatype.gshell.help;

import org.sonatype.gshell.command.descriptor.HelpPageDescriptor;

import java.util.EventObject;

/**
 * Event fired once a help meta page has been added.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class MetaHelpPageAddedEvent
    extends EventObject
{
    ///CLOVER:OFF

    private final HelpPageDescriptor desc;

    public MetaHelpPageAddedEvent(final HelpPageDescriptor desc) {
        super(desc);

        assert desc != null;
        this.desc = desc;
    }

    public HelpPageDescriptor getDescriptor() {
        return desc;
    }
}