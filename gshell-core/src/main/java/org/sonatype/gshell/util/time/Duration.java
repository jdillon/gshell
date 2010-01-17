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

package org.sonatype.gshell.util.time;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * A representation of an immutable duration of time.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class Duration
    implements Serializable
{
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    public final long value;

    public final TimeUnit unit;

    public Duration(final long value, final TimeUnit unit) {
        this.value = value;

        if (unit == null) {
            this.unit = DEFAULT_UNIT;
        }
        else {
            this.unit = unit;
        }
    }

    public Duration(final long value) {
        this(value, DEFAULT_UNIT);
    }

    public long getValue() {
        return value;
    }

    public TimeUnit getUnit() {
        return unit;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Duration duration = (Duration) obj;

        return value == duration.value && unit == duration.unit;
    }

    public int hashCode() {
        int result;

        result = (int) (value ^ (value >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);

        return result;
    }

    public String toString() {
        return DurationFormatUtils.formatDurationHMS(value);
    }
}
