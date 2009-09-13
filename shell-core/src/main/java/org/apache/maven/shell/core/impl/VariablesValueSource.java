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

package org.apache.maven.shell.core.impl;

import org.apache.maven.shell.ShellContextHolder;
import org.apache.maven.shell.Variables;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ValueSource} for {@link Variables}.
 *
 * @version $Rev$ $Date$
 */
public class VariablesValueSource
    extends AbstractValueSource
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public VariablesValueSource() {
        super(false);
    }

    public Object getValue(final String expression) {
        Variables vars = ShellContextHolder.get().getVariables();
        return vars.get(expression);
    }
}