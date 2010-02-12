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

import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

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

    private MessageSource messages;

    public AliasHelpPage(final String name, final String alias) {
        assert name != null;
        this.name = name;
        assert alias != null;
        this.alias = alias;
    }

    private MessageSource getMessages() {
        if (messages == null) {
            messages = new ResourceBundleMessageSource(getClass());
        }
        return messages;
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return getMessages().format("alias-description", alias);
    }

    public void render(final PrintWriter out) {
        assert out != null;
        out.println(getMessages().format("alias-content", name, alias));
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}