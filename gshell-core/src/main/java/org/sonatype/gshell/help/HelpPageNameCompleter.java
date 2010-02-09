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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import jline.console.Completer;
import jline.console.completers.AggregateCompleter;

/**
 * {@link jline.console.Completer} for help page names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class HelpPageNameCompleter
    extends AggregateCompleter
{
    @Inject
    public HelpPageNameCompleter(final @Named("alias-name") Completer c1,
                                 final @Named("node-path") Completer c2,
                                 final @Named("meta-help-page-name") Completer c3)
    {
        super(c1, c2, c3);
    }
}