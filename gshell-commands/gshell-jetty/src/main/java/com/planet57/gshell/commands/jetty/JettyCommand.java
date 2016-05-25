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
package com.planet57.gshell.commands.jetty;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Maps;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.support.CommandActionSupport;
import com.planet57.gshell.util.NameValue;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.pref.Preferences;
import jline.console.completer.Completer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Start a <a href="http://eclipse.org/jetty">Jetty</a> server.
 *
 * @since 2.6.5
 */
@Command(name = "jetty")
@Preferences(path = "commands/jetty")
public class JettyCommand
    extends CommandActionSupport
{
  @Option(name = "w", longName = "wait")
  private boolean wait;

  @Argument(required = true)
  private File file;

  private Map<String, String> properties;

  @Option(name = "D", longName = "define")
  protected void setVariable(final String input) {
    if (properties == null) {
      properties = Maps.newHashMap();
    }
    NameValue nv = NameValue.parse(input);
    properties.put(nv.name, nv.value);
  }

  @Inject
  public void installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1, null);
  }

  public Object execute(final CommandContext context) throws Exception {
    checkNotNull(context);

    if (context.getVariables().contains(Server.class)) {
      log.error("Jetty server is already running");
      return false;
    }

    log.info("Loading configuration: {}", file);

    XmlConfiguration config = new XmlConfiguration(Resource.newResource(file.toURI()).getURL());
    Map<String, String> props = config.getProperties();
    if (properties != null) {
      props.putAll(properties);
    }
    props.put("shell.home", context.getShell().getBranding().getShellHomeDir().getAbsolutePath());

    if (log.isErrorEnabled()) {
      log.debug("Custom properties:");
      for (Map.Entry<String, String> entry : props.entrySet()) {
        log.debug("  {}='{}'", entry.getKey(), entry.getValue());
      }
    }

    // HACK: Promote to System properties
    for (Map.Entry<String, String> entry : props.entrySet()) {
      System.setProperty(entry.getKey(), entry.getValue());
    }

    Object obj = config.configure();
    log.debug("Configured object: {}", obj);

    if (obj instanceof LifeCycle) {
      LifeCycle lc = (LifeCycle) obj;
      if (!lc.isRunning()) {
        lc.start();
        log.info("Started");
      }
    }

    context.getVariables().set(Server.class, obj);

    if (wait) {
      synchronized (obj) {
        obj.wait();
      }
    }

    return obj;
  }
}