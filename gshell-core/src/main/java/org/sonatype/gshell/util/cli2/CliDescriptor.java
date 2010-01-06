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

package org.sonatype.gshell.util.cli2;

import org.sonatype.gshell.util.AnnotationDescriptor;
import org.sonatype.gshell.util.cli2.handler.Handler;
import org.sonatype.gshell.util.setter.Setter;

/**
 * Base-class for CLI descriptors.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public abstract class CliDescriptor
    extends AnnotationDescriptor
{
    private final Setter setter;

    private final String token;

    private final boolean required;

    private final String description;

    private final String defaultValue;

    private final Class handlerType;

    public CliDescriptor(final Object spec, final Setter setter) {
        assert spec != null;
        assert setter != null;
        this.setter = setter;

        if (spec instanceof Option) {
            Option opt = (Option)spec;
            token = UNINITIALIZED_STRING.equals(opt.token()) ? null : opt.token();
            required = opt.required();
            description = UNINITIALIZED_STRING.equals(opt.description()) ? null : opt.description();
            defaultValue = UNINITIALIZED_STRING.equals(opt.defaultValue()) ? null : opt.defaultValue();
            handlerType = Handler.class == opt.handler() ? null : opt.handler();
        }
        else if (spec instanceof Argument) {
            Argument arg = (Argument)spec;
            token = UNINITIALIZED_STRING.equals(arg.token()) ? null : arg.token();
            required = arg.required();
            description = UNINITIALIZED_STRING.equals(arg.description()) ? null : arg.description();
            defaultValue = UNINITIALIZED_STRING.equals(arg.defaultValue()) ? null : arg.defaultValue();
            handlerType = Handler.class == arg.handler() ? null : arg.handler();
        }
        else {
            throw new IllegalArgumentException("Invalid spec: " + spec);
        }
    }

    public String getId() {
        return setter.getName();
    }

    public Setter getSetter() {
        return setter;
    }

    public Class getType() {
        return setter.getBean().getClass();
    }

    public String getToken() {
        return token;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Class getHandlerType() {
        return handlerType;
    }


    public boolean isArgument() {
        return this instanceof ArgumentDescriptor;
    }

    public boolean isOption() {
        return this instanceof OptionDescriptor;
    }

    public String getMessageCode() {
        if (isArgument()) {
            return String.format("argument.%s", getId());
        }
        else {
            return String.format("option.%s", getId());
        }
    }

    public String getTokenCode() {
        if (isArgument()) {
            return String.format("argument.%s.token", getId());
        }
        else {
            return String.format("option.%s.token", getId());
        }
    }
}