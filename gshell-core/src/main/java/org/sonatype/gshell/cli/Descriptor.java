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

import org.sonatype.gshell.cli.handler.Handler;

/**
 * Basic container for option and argument descriptors.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class Descriptor
{
    private final String id;

    private final String description;

    private final String token;

    private final boolean required;

    private final boolean multiValued;

    private final Class<? extends Handler> handlerType;

    protected Descriptor(final String id, final String description, final String token, final boolean required, final Class<? extends Handler> handlerType, final boolean multiValued) {
        assert id != null;
        this.id = id;

        // Handle "" = null, since default values in annotations cannot be set to null
        if (description != null && description.length() == 0) {
            this.description = null;
        }
        else {
            this.description = description;
        }

        if (token != null && token.length() == 0) {
            this.token = null;
        }
        else {
            this.token = token;
        }

        this.required = required;
        this.multiValued = multiValued;

        // On IBM JDK, the value passed is null instead of the default value, so fix it in case
        this.handlerType = handlerType != null ? handlerType : Handler.class;
    }

    public String getId() {
        return id;
    }

    public String getMessageCode() {
        if (this instanceof ArgumentDescriptor) {
            return String.format("argument.%s", id);
        }
        else {
            return String.format("option.%s", id);
        }
    }

    public String getTokenCode() {
        if (this instanceof ArgumentDescriptor) {
            return String.format("argument.%s.token", id);
        }
        else {
            return String.format("option.%s.token", id);
        }
    }

    public String getDescription() {
        return description;
    }

    public String getToken() {
        return token;
    }

    public boolean isRequired() {
        return required;
    }

    public Class<? extends Handler> getHandlerType() {
        return handlerType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }
}