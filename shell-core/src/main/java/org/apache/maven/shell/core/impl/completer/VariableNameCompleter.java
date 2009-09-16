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
import org.apache.maven.shell.ShellHolder;
import org.apache.maven.shell.Variables;
import org.apache.maven.shell.console.completer.StringsCompleter;
import org.codehaus.plexus.component.annotations.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * {@link jline.Completor} for {@link Application} variable names.
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role=Completor.class, hint="variable-name")
public class VariableNameCompleter
    implements Completor
{
    public int complete(final String buffer, final int cursor, final List candidates) {
        Variables vars = ShellHolder.get().getVariables();

        // There are no events for variables muck, so each time we have to rebuild the list.
        StringsCompleter delegate = new StringsCompleter();
        Collection<String> strings = delegate.getStrings();

        Iterator<String> iter = vars.names();
        while (iter.hasNext()) {
            String name = iter.next();
            strings.add(name);
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}