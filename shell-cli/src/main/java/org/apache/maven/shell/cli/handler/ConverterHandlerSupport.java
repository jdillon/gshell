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
import org.apache.xbean.propertyeditor.PropertyEditors;
import org.apache.xbean.propertyeditor.PropertyEditorException;

/**
 * A generic {@link Handler} which uses XBean Reflect to convert values.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConverterHandlerSupport
    extends Handler<Object>
{
    private final Class type;

    private final String token;

    public ConverterHandlerSupport(final Descriptor desc, final Setter<Object> setter, final Class type, final String token) {
        super(desc, setter);
        assert type != null;
        this.type = type;
        assert token != null;
        this.token = token;
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;

        try {
            Object value = PropertyEditors.getValue(type, params.get(0));
            setter.set(value);
        }
        catch (PropertyEditorException e) {
            throw new ProcessingException(e.getCause());
        }

        return 1;
    }

    @Override
    public String getDefaultToken() {
        return token;
    }
}