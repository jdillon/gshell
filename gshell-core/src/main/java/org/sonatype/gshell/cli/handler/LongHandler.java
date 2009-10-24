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
import org.sonatype.gshell.cli.ProcessingException;
import org.sonatype.gshell.cli.setter.Setter;

/**
 * Handler for integer types.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class LongHandler
    extends Handler<Long>
{
    public LongHandler(final Descriptor desc, final Setter<? super Long> setter) {
        super(desc, setter);
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;

        String token = params.get(0);
        long value = Long.parseLong(token);
        getSetter().set(value);

        return 1;
    }

    @Override
    public String getDefaultToken() {
        return "N";
    }
}