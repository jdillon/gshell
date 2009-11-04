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

package org.sonatype.gshell.cli.handler;

import org.sonatype.gshell.cli.Descriptor;
import org.sonatype.gshell.cli.OptionDescriptor;
import org.sonatype.gshell.cli.Parameters;
import org.sonatype.gshell.cli.ProcessingException;
import org.sonatype.gshell.util.converter.Converters;
import org.sonatype.gshell.util.setter.Setter;

/**
 * Handler for boolean types.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class BooleanHandler
    extends Handler<Boolean>
{
    public BooleanHandler(final Descriptor desc, final Setter setter) {
        super(desc, setter);
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;

        if (isArgument() || ((OptionDescriptor)getDescriptor()).isArgumentRequired()) {
            getSetter().set(Converters.getValue(Boolean.class, params.get(0)));

            return 1;
        }
        else {
            getSetter().set(true);

            return 0;
        }
    }

    @Override
    public String getDefaultToken() {
        return null;
    }
}
