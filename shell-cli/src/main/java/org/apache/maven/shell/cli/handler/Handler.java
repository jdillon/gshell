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
import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.cli.OptionDescriptor;
import org.apache.maven.shell.cli.setter.Setter;
import org.apache.maven.shell.i18n.MessageSource;
import org.apache.maven.shell.i18n.ResourceNotFoundException;

/**
 * Provides the basic mechanism to handle custom option and argument processing.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class Handler<T>
{
    private final Descriptor descriptor;
    
    private final Setter<? super T> setter;

    private boolean kvp = false;

    protected Handler(final Descriptor descriptor, final Setter<? super T> setter) {
        assert descriptor != null;
        this.descriptor = descriptor;
        assert setter != null;
        this.setter = setter;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public Setter<? super T> getSetter() {
        return setter;
    }

    public Boolean isKeyValuePair() {
        return kvp;
    }

    public void setKeyValuePair(final boolean flag) {
        this.kvp = flag;
    }

    public abstract int handle(Parameters params) throws ProcessingException;

    public abstract String getDefaultToken();

    public String getToken(final MessageSource messages) { 
        // messages may be null

        String token = descriptor.getToken();

        // If we have i18n messages for the command, then try to resolve the token further
        if (messages != null) {
            String code = token;

            // If there is no coded, then generate one
            if (code == null) {
                code = descriptor.getTokenCode();
            }

            // Resolve the text in the message source
            try {
                token = messages.getMessage(code);
            }
            catch (ResourceNotFoundException e) {
                // Just use the code as the message
            }
        }

        if (token == null) {
            token = getDefaultToken();
        }

        return token;
    }

    public String getHelpText(final MessageSource messages) {
        // messages may be null

        String message = descriptor.getDescription();

        // If we have i18n messages for the command, then try to resolve the message further using the message as the code
        if (messages != null) {
            String code = message;

            // If there is no code, then generate one
            if (code == null) {
                code = descriptor.getMessageCode();
            }

            // Resolve the text in the message source
            try {
                message = messages.getMessage(code);
            }
            catch (ResourceNotFoundException e) {
                // Just use the code as the message
            }
        }

        return message;
    }

    public boolean isArgument() {
        return descriptor instanceof ArgumentDescriptor;
    }

    public boolean isOption() {
        return descriptor instanceof OptionDescriptor;
    }
}
