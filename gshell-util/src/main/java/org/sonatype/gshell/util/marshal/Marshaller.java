/*
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
package org.sonatype.gshell.util.marshal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

/**
 * Marshaller interface.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public interface Marshaller<T>
{
    void marshal(T root, OutputStream output);

    void marshal(T root, Writer writer);

    String marshal(T root);

    void marshal(T root, File file) throws IOException;

    T unmarshal(InputStream input);

    T unmarshal(Reader reader);

    T unmarshal(String xml);

    T unmarshal(URL url) throws IOException;

    T unmarshal(File file) throws IOException;
}