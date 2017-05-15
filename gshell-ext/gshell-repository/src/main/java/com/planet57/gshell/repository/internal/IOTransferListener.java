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
package com.planet57.gshell.repository.internal;

import com.planet57.gshell.util.io.IO;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.sonatype.goodies.common.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link IO} {@link TransferListener}.
 * 
 * @since 3.0
 */
public class IOTransferListener
  extends ComponentSupport
  implements TransferListener
{
  private final IO io;

  public IOTransferListener(final IO io) {
    this.io = checkNotNull(io);
  }

  @Override
  public void transferInitiated(final TransferEvent event) throws TransferCancelledException {
    io.format("Transfer initiated: %s%n", event);
  }

  @Override
  public void transferStarted(final TransferEvent event) throws TransferCancelledException {
    io.format("Transfer started: %s%n", event);
  }

  @Override
  public void transferProgressed(final TransferEvent event) throws TransferCancelledException {
    io.format("Transfer progressed: %s%n", event);
  }

  @Override
  public void transferCorrupted(final TransferEvent event) throws TransferCancelledException {
    io.format("Transfer corrupted: %s%n", event);
  }

  @Override
  public void transferSucceeded(final TransferEvent event) {
    io.format("Transfer succeeded: %s%n", event);
  }

  @Override
  public void transferFailed(final TransferEvent event) {
    io.format("Transfer failed: %s%n", event);
  }
}
