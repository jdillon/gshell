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

package org.apache.maven.shell.core;

/**
 * Container and parser for <tt>name=value</tt> bits.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NameValue
{
    public final String name;

    public final String value;

    private NameValue(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static NameValue parse(final String input) {
        assert input != null;

        String name, value;

        int i = input.indexOf('=');
        if (i == -1) {
            name = input;
            value = Boolean.TRUE.toString();
        }
        else {
            name = input.substring(0, i);
            value = input.substring(i + 1, input.length());
        }

        return new NameValue(name.trim(), value);
    }
}