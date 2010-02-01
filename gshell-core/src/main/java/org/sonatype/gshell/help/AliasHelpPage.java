/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.help;

import java.io.PrintWriter;

/**
 * {@link HelpPage} for an alias.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class AliasHelpPage
    implements HelpPage
{
    private final String name;

    private final String alias;

    public AliasHelpPage(final String name, final String alias) {
        assert name != null;
        this.name = name;
        assert alias != null;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getBriefDescription() {
        return String.format("Alias to: @|bold %s|@", alias); // TODO: i18n
    }

    public void render(final PrintWriter out) {
        assert out != null;

        out.format("The @|bold %s|@ command is an alias to: @|bold %s|@", name, alias); // TODO: i18n
        out.println();
    }
}