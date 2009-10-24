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

package org.sonatype.gshell;

import java.io.IOException;
import java.util.List;

/**
 * Provides access to a shells history.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public interface History
{
    void add(String item);

    /**
     * Clear the history elements for the current shell's context.
     */
    void clear();

    /**
     * Clear the history elements for the current shell's context and purge any persistent storage.
     *
     * @throws IOException
     */
    void purge() throws IOException;

    int size();
    
    /**
     * Returns a list of all history items.
     * 
     * @return  List of history elements; never null
     */
    List<String> items();
}