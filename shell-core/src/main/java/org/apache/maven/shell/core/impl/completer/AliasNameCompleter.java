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

package org.apache.maven.shell.core.impl.completer;

import jline.Completor;
import org.apache.maven.shell.console.completer.StringsCompleter;
import org.apache.maven.shell.registry.AliasRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Collection;
import java.util.List;

/**
 * {@link Completor} for alias names.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Completor.class, hint="alias-name")
public class AliasNameCompleter
    implements Completor, Initializable
{
    @Requirement
    private AliasRegistry aliasRegistry;

    private final StringsCompleter delegate = new StringsCompleter();

    @Override
    public void initialize() throws InitializationException {
        assert aliasRegistry != null;
        Collection<String> names = aliasRegistry.getAliasNames();
        delegate.getStrings().addAll(names);
    }

    public int complete(final String buffer, final int cursor, final List candidates) {
        return delegate.complete(buffer, cursor, candidates);
    }
}