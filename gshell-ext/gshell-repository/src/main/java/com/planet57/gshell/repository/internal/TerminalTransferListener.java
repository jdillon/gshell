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
import org.eclipse.aether.transfer.TransferResource;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.Display;
import org.sonatype.goodies.common.ByteSize;
import org.sonatype.goodies.common.ComponentSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Terminal} {@link TransferListener}.
 * 
 * @since 3.0
 */
public class TerminalTransferListener
  extends ComponentSupport
  implements TransferListener
{
  private final Terminal terminal;

  private final Display display;

  private final Map<TransferResource, TransferEvent> transfers = Collections.synchronizedMap(new LinkedHashMap<TransferResource, TransferEvent>());

  public TerminalTransferListener(final Terminal terminal) {
    this.terminal = checkNotNull(terminal);
    display = new Display(terminal, false);
    display.resize(terminal.getHeight(), terminal.getWidth());
  }

  public TerminalTransferListener(final IO io) {
    this(io.terminal);
  }

  private void redisplay() {
    synchronized (transfers) {
      List<AttributedString> messages;
      if (transfers.isEmpty()) {
        messages = Collections.emptyList();
      }
      else {
        messages = new ArrayList<>(transfers.size());
        AttributedStringBuilder buff = new AttributedStringBuilder();
        transfers.forEach((transfer, event) -> {
          buff.append(transfer.getRepositoryUrl());
          buff.append(transfer.getResourceName());

          long bytes = event.getTransferredBytes();
          if (bytes > 0) {
            ByteSize size = ByteSize.bytes(bytes);
            buff.append(": ").append(String.valueOf(size));
          }

          messages.add(buff.toAttributedString());
          buff.setLength(0);
        });
      }

      display.update(messages, -1, true);
    }
  }

  private void track(final TransferEvent event) {
    transfers.put(event.getResource(), event);
  }

  private void untrack(final TransferEvent event) {
    transfers.remove(event.getResource());
  }

  @Override
  public void transferInitiated(final TransferEvent event) throws TransferCancelledException {
    track(event);
    redisplay();
  }

  @Override
  public void transferStarted(final TransferEvent event) throws TransferCancelledException {
    track(event);
    redisplay();
  }

  @Override
  public void transferProgressed(final TransferEvent event) throws TransferCancelledException {
    track(event);
    redisplay();
  }

  @Override
  public void transferCorrupted(final TransferEvent event) throws TransferCancelledException {
    track(event);
    redisplay();
  }

  @Override
  public void transferSucceeded(final TransferEvent event) {
    untrack(event);
    redisplay();
  }

  @Override
  public void transferFailed(final TransferEvent event) {
    untrack(event);
    redisplay();
  }
}
