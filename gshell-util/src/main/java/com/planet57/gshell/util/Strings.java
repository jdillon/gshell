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
package com.planet57.gshell.util;

/**
 * Common <code>String</code> manipulation routines from <tt>org.codehaus.plexus:plexus-utils</tt>.
 *
 * @since 2.0
 */
@Deprecated
public class Strings
{
  // FIXME: replace with guava Joiner

  public static String join(final Object[] array, String separator) {
    if (separator == null) {
      separator = "";
    }
    int arraySize = array.length;
    int bufSize = (arraySize == 0 ? 0 : (array[0].toString().length() + separator.length()) * arraySize);
    StringBuilder buff = new StringBuilder(bufSize);

    for (int i = 0; i < arraySize; i++) {
      if (i > 0) {
        buff.append(separator);
      }
      buff.append(array[i]);
    }

    return buff.toString();
  }
}
