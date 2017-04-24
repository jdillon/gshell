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
package com.planet57.gshell;

import com.planet57.gossip.Log;
import com.planet57.gshell.command.CommandAction;
import org.slf4j.Logger;

import javax.annotation.Nullable;

/**
 * Helper to decode exit codes.
 *
 * @since 3.0
 */
public class ExitCodeDecoder
{
  private static final Logger log = Log.getLogger(ExitCodeDecoder.class);

  public static int decode(@Nullable final Object result) {
    log.debug("Decoding: {}", result);

    if (result instanceof CommandAction.Result) {
      return ((CommandAction.Result) result).ordinal();
    }
    else if (result instanceof Number) {
      return ((Number) result).intValue();
    }
    else if (result == null) {
      return 0;
    }
    else {
      return 1;
    }
  }
}
