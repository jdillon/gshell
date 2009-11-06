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

package org.sonatype.gshell.cli;

/**
 * Descriptor for {@link Argument} annotations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class ArgumentDescriptor
    extends Descriptor
{
    public ArgumentDescriptor(final String id, final Argument argument, final boolean forceMultiValued) {
        super(id, argument.description(), argument.token(), argument.required(), argument.handler(), argument.multiValued() || forceMultiValued);
    }

    @Override
    public String toString() {
        String tmp = getToken();
        if (tmp != null && tmp.length() != 0) {
            return tmp;
        }

        return "ARG";
    }
}
