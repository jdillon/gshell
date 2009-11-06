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

package org.sonatype.gshell.core.command;

import jline.console.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.NameAware;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

import java.util.List;

/**
 * Provides support for {@link CommandAction} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class CommandActionSupport
    implements CommandAction, NameAware
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String name;

    private MessageSource messages;

    private Completer[] completers;

    public String getName() {
        if (name == null) {
            throw new IllegalStateException("Name was not configured");
        }
        return name;
    }

    public void setName(final String name) {
        assert name != null;
        if (this.name != null) {
            throw new IllegalStateException("Name was already configured");
        }
        this.name = name;
    }

    public MessageSource getMessages() {
        if (messages == null) {
            messages = new ResourceBundleMessageSource(getClass());
        }

        return messages;
    }

    public Completer[] getCompleters() {
        return completers;
    }

    public void setCompleters(final Completer... completers) {
        this.completers = completers;
    }

    public void setCompleters(final List<Completer> completers) {
        assert completers != null;
        setCompleters(completers.toArray(new Completer[completers.size()]));
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public CommandAction copy() {
        return (CommandAction) clone();
    }
}