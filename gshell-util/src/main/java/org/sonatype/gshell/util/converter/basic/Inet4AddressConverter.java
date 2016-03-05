/*
 * Copyright (c) 2009-present the original author or authors.
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
package org.sonatype.gshell.util.converter.basic;

import org.sonatype.gshell.util.converter.ConverterSupport;

import java.net.Inet4Address;

/**
 * Converter for {@link Inet4Address} types.
 *
 * @since 2.0
 */
public class Inet4AddressConverter
    extends ConverterSupport
{
    public Inet4AddressConverter() {
        super(Inet4Address.class);
    }

    protected Object convertToObject(final String text) throws Exception {
        return Inet4Address.getByName(text);
    }
}
