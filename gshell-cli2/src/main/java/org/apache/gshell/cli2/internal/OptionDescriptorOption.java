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

package org.apache.gshell.cli2.internal;

import org.apache.gshell.cli2.OptionDescriptor;

/**
 * Support for Commons Cli option processing to use an {@link org.apache.gshell.cli.OptionDescriptor}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class OptionDescriptorOption
    extends org.apache.commons.cli.Option
{
    private final OptionDescriptor descriptor;

    public OptionDescriptorOption(final OptionDescriptor descriptor) {
        super(descriptor.getOpt(), descriptor.getDescription());

        this.descriptor = descriptor;

        this.setRequired(descriptor.isRequired());
        this.setLongOpt(descriptor.getLongOpt());
        this.setOptionalArg(descriptor.isArgOptional());
        this.setArgs(descriptor.getArgs());
        this.setArgName(descriptor.getArgName());
        this.setValueSeparator(descriptor.getValueSeperator());
    }

    public OptionDescriptor getDescriptor() {
        return descriptor;
    }
}