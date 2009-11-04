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

package org.sonatype.gshell.util.converter.basic;

import org.sonatype.gshell.util.converter.ConverterSupport;

import java.math.BigInteger;

/**
 * Converter for {@link BigInteger} types.
 *
 * @since 2.0
 */
public class BigIntegerConverter
    extends ConverterSupport
{
    public BigIntegerConverter() {
        super(BigInteger.class);
    }

    protected Object toObjectImpl(final String value) throws Exception {
        return new BigInteger(value);
    }
}
