/*
 * Copyright (c) 2009-present the original author or authors.
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
package org.sonatype.gshell.util.i18n;

import org.slf4j.Logger;
import org.sonatype.gossip.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A message source which aggregates messages sources in order.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class AggregateMessageSource
    implements MessageSource
{
    private static final Logger log = Log.getLogger(AggregateMessageSource.class);

    private final List<MessageSource> sources = new LinkedList<MessageSource>();

    public AggregateMessageSource(final List<MessageSource> sources) {
        assert sources != null;
        this.sources.addAll(sources);
    }

    public AggregateMessageSource(final MessageSource... sources) {
        this(Arrays.asList(sources));
    }

    public List<MessageSource> getSources() {
        return sources;
    }

    public String getMessage(final String code) {
        String result = null;

        for (MessageSource source : sources) {
            try {
                result = source.getMessage(code);
                if (result != null) {
                    break;
                }
            }
            catch (ResourceNotFoundException e) {
                log.trace(e.toString(), e);
            }
        }

        if (result == null) {
            throw new ResourceNotFoundException(code);
        }

        return result;
    }

    public String format(final String code, final Object... args) {
        String result = null;

        for (MessageSource source : sources) {
            try {
                result = source.format(code, args);
                if (result != null) {
                    break;
                }
            }
            catch (ResourceNotFoundException e) {
                log.trace(e.toString(), e);
            }
        }

        if (result == null) {
            throw new ResourceNotFoundException(code);
        }

        return result;
    }
}