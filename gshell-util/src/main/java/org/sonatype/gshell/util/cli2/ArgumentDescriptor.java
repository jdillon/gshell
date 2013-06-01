/*
 * Copyright (c) 2009-2013 the original author or authors.
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
package org.sonatype.gshell.util.cli2;

import org.sonatype.gshell.util.setter.Setter;

/**
 * {@link Argument} descriptor.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class ArgumentDescriptor
    extends CliDescriptor
{
    private final Argument spec;

    public ArgumentDescriptor(final Argument spec, final Setter setter) {
        super(spec, setter);
        assert spec != null;
        this.spec = spec;
    }

    public Argument getSpec() {
        return spec;
    }

    public int getIndex() {
        return spec.index();
    }

    @Override
    public String getSyntax() {
        String tmp = getToken();
        if (tmp != null && tmp.length() != 0) {
            return tmp;
        }

        return "ARG";
    }
}