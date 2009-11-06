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

package org.sonatype.gshell.util.setter;

import org.sonatype.gshell.i18n.MessageSource;
import org.sonatype.gshell.i18n.ResourceBundleMessageSource;

/**
 * Messages for the {@link org.sonatype.gshell.util.setter} package.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
enum Messages
{
    ///CLOVER:OFF

    ILLEGAL_METHOD_SIGNATURE,
    ILLEGAL_FIELD_SIGNATURE,;

    private final MessageSource messages = new ResourceBundleMessageSource(getClass());

    String format(final Object... args) {
        return messages.format(name(), args);
    }
}