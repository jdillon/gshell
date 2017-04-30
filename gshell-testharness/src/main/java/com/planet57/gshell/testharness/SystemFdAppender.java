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
package com.planet57.gshell.testharness;

import ch.qos.logback.core.OutputStreamAppender;
import com.planet57.gshell.util.io.StreamSet;
import org.apache.felix.service.threadio.ThreadIO;

/**
 * Append to {@link StreamSet#SYSTEM_FD} output-stream.
 *
 * This is needed to prevent logging from interfering with {@link ThreadIO} and command output.
 *
 * @since 3.0
 */
public class SystemFdAppender<E>
  extends OutputStreamAppender<E>
{
  // FIXME: this creates some issues with maven it seems:
  // FIXME: [INFO] Running com.planet57.gshell.commands.standard.AliasActionTest
  // FIXME: [WARNING] Corrupted stdin stream in forked JVM 1. See the dump file ...

  @Override
  public void start() {
    setOutputStream(StreamSet.SYSTEM_FD.out);
    super.start();
  }
}
