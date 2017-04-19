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
package com.planet57.gshell.util.io;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Stream/Reader/Writer copy helpers.
 *
 * Based on plexus-utils IOUtil.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.6.4
 */
@Deprecated
public class Copier
{
  public static void copy(final InputStream input, final OutputStream output) throws IOException {
    ByteStreams.copy(input, output);
  }

  public static void copy(final Reader input, final Appendable output) throws IOException {
    CharStreams.copy(input, output);
  }
}
