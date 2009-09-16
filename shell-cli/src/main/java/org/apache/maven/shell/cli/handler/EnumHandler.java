/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell.cli.handler;

import org.apache.maven.shell.cli.Descriptor;
import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.cli.setter.Setter;

/**
 * Handler for enum types.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EnumHandler<T extends Enum<T>>
    extends Handler<T>
{
    private final Class<T> enumType;

    public EnumHandler(final Descriptor desc, final Setter<? super T> setter, final Class<T> enumType) {
        super(desc, setter);

        assert enumType != null;
        
        this.enumType = enumType;
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;
        
        String token = params.get(0);
        T value = null;

        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(token)) {
                value = constant;
                break;
            }
        }

        if (value == null) {
            throw new ProcessingException(Messages.ILLEGAL_OPERAND.format(descriptor.toString(), token));
        }

        setter.set(value);
        
        return 1;
    }

    @Override
    public String getDefaultToken() {
        StringBuilder buff = new StringBuilder();
        buff.append("[");

        for (T constants : enumType.getEnumConstants()) {
            buff.append(constants).append(" | ");
        }

        buff.delete(buff.length()-3, buff.length());
        buff.append("]");

        return buff.toString();
    }
}
