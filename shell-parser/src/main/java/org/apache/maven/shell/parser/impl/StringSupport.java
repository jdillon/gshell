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

package org.apache.maven.shell.parser.impl;

/**
 * Support for string types.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class StringSupport
    extends SimpleNode
{
    protected Token token;

    public StringSupport(final int id) {
        super(id);
    }

    public StringSupport(final Parser p, final int id) {
        super(p, id);
    }

    public void setToken(final Token token) {
        assert token != null;

        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        Token t = getToken();
        if (t == null) {
            throw new IllegalStateException("Token not set");
        }

        return t.image;
    }

    public String toString() {
        return String.format("%s (%s)", super.toString(), getToken());
    }

    /**
     * Returns an unquoted value.
     *
     * @param value     String to unquote, must not be null; length must be at least 2
     * @return          Unquoted value
     */
    protected String unquote(final String value) {
        assert value != null;
        assert value.length() >= 2;

        return value.substring(1, value.length() - 1);
    }
}