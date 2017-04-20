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

import java.io.Closeable;
import java.io.IOException;

import com.planet57.gossip.Log;
import org.slf4j.Logger;

import javax.annotation.Nullable;

/**
 * Quietly closes {@link Closeable} objects.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Closeables
{
  private static final Logger log = Log.getLogger(Closeables.class);

  public static void close(@Nullable final Closeable... targets) {
    if (targets != null) {
      for (Closeable c : targets) {
        if (c == null) {
          continue;
        }

        try {
          c.close();
        }
        catch (IOException e) {
          log.trace(e.toString(), e);
        }
      }
    }
  }
}
