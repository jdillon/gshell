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

package org.sonatype.gshell.commands.vfs;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.sonatype.gshell.builder.guice.CoreModule;
import org.sonatype.gshell.commands.CommandTestSupport;
import org.sonatype.gshell.shell.TestShellBuilder;
import org.sonatype.gshell.vfs.VfsModule;

import java.util.List;

/**
 * Support for testing VFS commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class VfsCommandTestSupport
    extends CommandTestSupport
{
    public VfsCommandTestSupport(final Class<?> type) {
        super(type);
    }

    @Override
    protected void configureModules(final List<Module> modules) {
        super.configureModules(modules);
        modules.add(new VfsModule());
    }
}