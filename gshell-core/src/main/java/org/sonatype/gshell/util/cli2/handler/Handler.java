/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.util.cli2.handler;

import org.sonatype.gshell.util.cli2.CliDescriptor;
import org.sonatype.gshell.util.converter.Converters;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceNotFoundException;
import org.sonatype.gshell.util.setter.Setter;

/**
 * Provides the basic mechanism to handle custom option and argument processing.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public abstract class Handler
{
    private final CliDescriptor descriptor;

    protected Handler(final CliDescriptor descriptor) {
        assert descriptor != null;
        this.descriptor = descriptor;
    }

    public CliDescriptor getDescriptor() {
        return descriptor;
    }

    public Setter getSetter() {
        return getDescriptor().getSetter();
    }

    protected void set(final Object value) throws Exception {
        getSetter().set(value);
    }

    protected void set(final String value) throws Exception {
        getSetter().set(Converters.getValue(getSetter().getType(), value));
    }

    public abstract static class Input
    {
        public abstract String[] getAll();

        public String get() {
            return getAll() == null ? null : getAll()[0];
        }
    }

    public abstract void handle(String arg) throws Exception;

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
}