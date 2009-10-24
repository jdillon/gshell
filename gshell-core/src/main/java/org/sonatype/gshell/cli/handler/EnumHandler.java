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
 * Handler for enum types.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EnumHandler<T extends Enum<T>>
    extends Handler<T>
{
    private final Class<T> type;

    public EnumHandler(final Descriptor desc, final Setter<? super T> setter, final Class<T> type) {
        super(desc, setter);

        assert type != null;
        this.type = type;
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;
        
        String token = params.get(0);
        T value = null;

        for (T constant : type.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(token)) {
                value = constant;
                break;
            }
        }

        if (value == null) {
            throw new ProcessingException(Messages.ILLEGAL_OPERAND.format(getDescriptor().toString(), token));
        }

        getSetter().set(value);
        
        return 1;
    }

    @Override
    public String getDefaultToken() {
        StringBuilder buff = new StringBuilder();
        buff.append('[');

        T[] constants = type.getEnumConstants();
        
        for (int i=0; i<constants.length; i++) {
            buff.append(constants[i].name().toLowerCase());
            if (i+1<constants.length) {
                buff.append('|');
            }
        }

        buff.append(']');

        return buff.toString();
    }
}
