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
package org.sonatype.gshell.util.yarn;

/**
 * Renders objects as strings.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class Yarn
{
    public static enum Style
    {
        DEFAULT(ToStringStyle.DEFAULT_STYLE),
        SHORT(ToStringStyle.SHORT_PREFIX_STYLE),
        MULTI(ToStringStyle.MULTI_LINE_STYLE);

        private final ToStringStyle target;

        Style(final ToStringStyle target) {
            this.target = target;
        }
    }

    public static String render(final Object target, Style style) {
        if (style == null) {
            style = Style.DEFAULT;
        }
        
        return ReflectionToStringBuilder.toString(target, style.target);
    }

    public static String render(final Object target) {
        return render(target, null);
    }
}