/*
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
package org.sonatype.gshell.command.support;

import jline.console.completer.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.resolver.NodePath;
import org.sonatype.gshell.util.NameAware;
import org.sonatype.gshell.util.i18n.MessageSource;
import org.sonatype.gshell.util.i18n.ResourceBundleMessageSource;

import java.util.List;
import java.util.MissingResourceException;

/**
 * Provides support for {@link org.sonatype.gshell.command.CommandAction} implementations.
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

    public String getSimpleName() {
        return new NodePath(getName()).last();
    }

    public MessageSource getMessages() {
        if (messages == null) {
            try {
                messages = createMessages();
            }
            catch (MissingResourceException e) {
                log.warn("Missing resources: " + e);

                messages = new MessageSource()
                {
                    public String getMessage(final String code) {
                        return code;
                    }

                    public String format(final String code, final Object... args) {
                        return code;
                    }
                };
            }
        }

        return messages;
    }

    /**
     * @since 2.4
     */
    protected ResourceBundleMessageSource createMessages() {
        return new ResourceBundleMessageSource(getClass());
    }

    public Completer[] getCompleters() {
        return completers;
    }

    public void setCompleters(final Completer... completers) {
        // completers can be null
        this.completers = completers;
    }

    public void setCompleters(final List<Completer> completers) {
        assert completers != null;
        setCompleters(completers.toArray(new Completer[completers.size()]));
    }

    public CommandAction clone() {
        try {
            return (CommandAction) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}