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

import org.apache.maven.shell.cli.ArgumentDescriptor;
import org.apache.maven.shell.cli.Descriptor;
import org.apache.maven.shell.cli.OptionDescriptor;
import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.cli.setter.Setter;

/**
 * Handler for boolean types.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class BooleanHandler
    extends Handler<Boolean>
{
    public BooleanHandler(final Descriptor desc, final Setter<? super Boolean> setter) {
        super(desc, setter);
    }

    @Override
    public int handle(final Parameters params) throws ProcessingException {
        assert params != null;
        
        if (descriptor instanceof ArgumentDescriptor) {
            String token = params.get(0);
            boolean value = Boolean.parseBoolean(token);
            setter.set(value);

            return 1;
        } 
        else if (descriptor instanceof OptionDescriptor && isKeyValuePair) {
            //
            // FIXME: This needs to be global to all handlers
            //
            String token = params.get(0);
            token = token.substring(token.indexOf('=') + 1, token.length());
            boolean value = Boolean.parseBoolean(token);
            setter.set(value);

            return 1;
        }
        else {
            if (((OptionDescriptor)descriptor).isArgumentRequired()) {
                String token = params.get(0);
                boolean value = Boolean.parseBoolean(token);
                setter.set(value);
    
                return 1;
            }
            else {
                setter.set(true);

                return 0;
            }
        }
    }

    @Override
    public String getDefaultToken() {
        return null;
    }
}
