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
package com.planet57.gshell.branding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import com.google.common.io.CharStreams;
import com.planet57.gshell.util.io.PrintBuffer;
import com.planet57.gshell.util.io.Closer;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link License) implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class LicenseSupport
    implements License
{
  private final String name;

  @Nullable
  private final URI uri;

  public LicenseSupport(final String name, @Nullable final URI uri) {
    this.name = checkNotNull(name);
    this.uri = uri;
  }

  public LicenseSupport(final String name, final String uri) {
    this(name, URI.create(uri));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  @Nullable
  public URI getUrl() {
    return uri;
  }

  @Override
  @Nullable
  public String getContent() throws IOException {
    if (uri == null) {
      return null;
    }

    PrintBuffer buff = new PrintBuffer();
    InputStream input = uri.toURL().openStream();
    try {
      CharStreams.copy(new InputStreamReader(input), buff);
    }
    finally {
      Closer.close(input);
    }
    return buff.toString();
  }

  @Override
  public String toString() {
    if (uri != null) {
      return String.format("%s - %s", name, uri);
    }
    return name;
  }
}
