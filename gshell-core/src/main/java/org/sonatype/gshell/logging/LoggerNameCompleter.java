/**
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
package org.sonatype.gshell.logging;

import javax.inject.Inject;
import javax.inject.Singleton;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import java.util.List;

/**
 * {@link Completer} for {@link Logger} names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Singleton
public class LoggerNameCompleter
    implements Completer
{
    private final LoggingSystem logging;

    @Inject
    public LoggerNameCompleter(final LoggingSystem logging) {
        assert logging != null;
        this.logging = logging;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        StringsCompleter delegate = new StringsCompleter();
        delegate.getStrings().addAll(logging.getLoggerNames());
        return delegate.complete(buffer, cursor, candidates);
    }
}