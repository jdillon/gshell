/**
 * Copyright (c) 2009-2011 the original author or authors.
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

import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Tests for {@link DateConverter}.
 */
public class DateConverterTest
{
    @Test
    public void testToObjectImpl() throws Exception {
        Date expected = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).parse("Mar 1, 1954");
        Date actual = (Date) new DateConverter().convertToObject("locale=en_US format=MEDIUM Mar 1, 1954");
        assertEquals(expected, actual);
    }

    @Test
    public void testFallbackFormats() throws Exception {
        DateConverter converter = new DateConverter();
        converter.convertToObject("2007-10-31");
        converter.convertToObject("2007-10-31T19:19:19PDT");
    }
}
