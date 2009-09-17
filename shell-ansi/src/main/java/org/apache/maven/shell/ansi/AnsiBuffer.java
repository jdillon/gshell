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

package org.apache.maven.shell.ansi;

/**
 * A stringbuffer-like thingy to help with using ANSI escape-codes.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiBuffer
{
    private final StringBuilder buff;

    public Boolean ansiEnabled;
    
    public boolean autoClear;

    public AnsiBuffer(final Boolean ansiEnabled, final boolean autoClear) {
        this.ansiEnabled = ansiEnabled;
        this.autoClear = autoClear;
        this.buff = new StringBuilder();
    }

    public AnsiBuffer() {
        this(null, true);
    }

    public String toString() {
        try {
            return buff.toString();
        }
        finally {
            if (autoClear) clear();
        }
    }

    public boolean isAnsiEnabled() {
        // Late bind the current system detected ANSI state
        if (ansiEnabled == null) {
            ansiEnabled = Ansi.isEnabled();
        }

        return ansiEnabled;
    }
    
    public void clear() {
        buff.setLength(0);
    }

    public int size() {
        return buff.length();
    }

    public AnsiBuffer append(final String text) {
        buff.append(text);

        return this;
    }

    public AnsiBuffer append(final Object obj) {
        return append(String.valueOf(obj));
    }

    public AnsiBuffer attrib(final AnsiCode code) {
        if (isAnsiEnabled()) {
            buff.append(AnsiCode.attrib(code));
        }

        return this;
    }

    public AnsiBuffer attrib(final String text, final AnsiCode code) {
        assert text != null;

        if (isAnsiEnabled()) {
            buff.append(AnsiCode.attrib(code)).append(text).append(AnsiCode.attrib(AnsiCode.OFF));
        }
        else {
            buff.append(text);
        }

        return this;
    }

    public AnsiBuffer attrib(final String text, final String codeName) {
        return attrib(text, AnsiCode.valueOf(codeName.toUpperCase()));
    }
}
