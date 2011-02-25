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

/**
 * Support for {@link Component} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public abstract class ComponentSupport
    implements Component
{
    private final String type;

    private final String name;

    public ComponentSupport(final String type, final String name) {
        assert type != null;
        this.type = type;
        assert name != null;
        this.name = name;
    }

    public ComponentSupport(final String type) {
        this(type, DEFAULT_NAME);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return (type + name).hashCode();
    }

    @Override
    public String toString() {
        return getType() + "{" + getName() + "}";
    }
}