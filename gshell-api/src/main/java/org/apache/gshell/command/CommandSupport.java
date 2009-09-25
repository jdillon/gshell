/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.command;

import jline.Completor;
import org.apache.gshell.i18n.MessageSource;
import org.apache.gshell.i18n.ResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// FIXME: This does not belong in api

/**
 * Provides support for {@link Command} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public abstract class CommandSupport
    implements Command, NameAware
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String name;

    private MessageSource messages;

    private Completor[] completers;

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

    public Completor[] getCompleters() {
        return completers;
    }

    public void setCompleters(final Completor... completers) {
        this.completers = completers;
    }

    public void setCompleters(final List<Completor> completers) {
        assert completers != null;
        setCompleters(completers.toArray(new Completor[completers.size()]));
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

    public Command copy() {
        return (Command)clone();
    }
}