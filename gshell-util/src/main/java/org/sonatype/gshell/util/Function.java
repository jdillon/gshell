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
package org.sonatype.gshell.util;

/**
 * A task that takes a value, returns a result and can throw an exception.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.4
 */
public interface Function<R,V,E extends Throwable>
{
    R invoke(V value) throws E;
}