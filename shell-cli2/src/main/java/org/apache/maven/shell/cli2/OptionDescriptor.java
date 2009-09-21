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

package org.apache.maven.shell.cli2;

import org.apache.maven.shell.cli2.setter.Setter;

/**
 * Descriptor for {@link Option} annotations.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class OptionDescriptor
    extends Descriptor
{
    private final Option option;

    public OptionDescriptor(final Option option, final Setter setter) {
        super(setter);
        this.option = option;
    }

    public String getDescription() {
        String tmp = option.description();
        if (tmp != null && tmp.length() != 0) {
            return tmp;
        }
        return null;
    }

    public String getOpt() {
        return option.opt();
    }

    public String getLongOpt() {
        String tmp = option.longOpt();
        if (tmp != null && tmp.length() != 0) {
            return tmp;
        }
        return null;
    }

    public boolean isRequired() {
        return option.required();
    }

    public String getArgName() {
        return option.argName();
    }

    public boolean isArgOptional() {
        return option.optionalArg();
    }

    public int getArgs() {
        return option.args();
    }

    public Character getValueSeperator() {
        return option.valuesep();
    }

    public Class getType() {
        Class tmp = option.type();
        if (tmp != Void.class) {
            return tmp;
        }
        return getSetter().getType();
    }
}