/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.util.prompt;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;
import org.sonatype.gshell.util.setter.Setter;

/**
 * Descriptor for {@link Prompt} annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class PromptDescriptor
{
    private static final Logger log = Log.getLogger(PromptDescriptor.class);

    private final Prompt spec;

    private final Setter setter;

    protected PromptDescriptor(final Prompt pref, final Setter setter) {
        assert pref != null;
        this.spec = pref;
        assert setter != null;
        this.setter = setter;
    }

    public Prompt getSpec() {
        return spec;
    }

    public Setter getSetter() {
        return setter;
    }

    public String getId() {
        return getSetter().getName();
    }
}