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
package com.planet57.gshell.commands.standard;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.planet57.gshell.branding.Branding;
import com.planet57.gshell.branding.License;
import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.util.cli2.Option;
import com.planet57.gshell.util.jline.TerminalHelper;
import com.planet57.gshell.util.pref.Preference;
import com.planet57.gshell.util.pref.Preferences;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderWriter;
import org.jline.reader.impl.completer.EnumCompleter;
import org.jline.terminal.Terminal;

import javax.annotation.Nonnull;

import static com.planet57.gshell.commands.standard.InfoAction.Section.SHELL;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

//
// Based on info command from Apache Felix
//

/**
 * Display shell information.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Command(name = "info")
@Preferences(path = "commands/info")
public class InfoAction
    extends CommandActionSupport
{
  private static final NumberFormat FMTI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));

  private static final NumberFormat FMTD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

  public enum Section
  {
    SHELL,
    TERMINAL,
    JVM,
    THREADS,
    MEMORY,
    CLASSES,
    OS,
    LICENSE,
  }

  @Preference
  @Option(longName = "pager", optionalArg = true)
  private boolean pager = false;

  @Preference
  @Argument()
  private List<Section> sections;

  @Option(name = "a", longName = "all")
  private boolean all;

  public InfoAction() {
    this.setCompleters(new EnumCompleter(Section.class));
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();
    Branding branding = context.getShell().getBranding();
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    ThreadMXBean threads = ManagementFactory.getThreadMXBean();
    MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
    ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();

    if (all) {
      sections = Arrays.asList(Section.values());
    }

    if (sections == null) {
      sections = Collections.singletonList(SHELL);
    }

    StringWriter buff = new StringWriter();
    PrintWriter writer = new PrintWriter(new AnsiRenderWriter(buff));

    // TODO: i18n all this
    for (Section section : sections) {
      switch (section) {
        case SHELL:
          printlnHeader(writer, "Shell");
          println(writer, "Display Name", branding.getDisplayName());
          println(writer, "Program Name", branding.getProgramName());
          println(writer, "License", branding.getLicense());
          println(writer, "Version", branding.getVersion());
          println(writer, "Home Dir", branding.getShellHomeDir());
          println(writer, "Context Dir", branding.getShellContextDir());
          println(writer, "User Home Dir", branding.getUserHomeDir());
          println(writer, "User Context Dir", branding.getUserContextDir());
          println(writer, "Script Extension", branding.getScriptExtension());
          println(writer, "Preference Path", branding.getPreferencesBasePath());
          println(writer, "Profile Script", branding.getProfileScriptName());
          println(writer, "Interactive Script", branding.getInteractiveScriptName());
          println(writer, "History File", branding.getHistoryFileName());
          println(writer, "ANSI", Ansi.isEnabled());
          break;

        case TERMINAL: {
          Terminal terminal = io.getTerminal();
          printlnHeader(writer, "Terminal");
          println(writer, "Name", terminal.getName());
          println(writer, "Type", terminal.getType());
          println(writer, "Echo", terminal.echo());
          println(writer, "Height", terminal.getHeight());
          println(writer, "Width", terminal.getWidth());
          println(writer, "Mouse support", terminal.hasMouseSupport());
          // TODO: attributes/capabilities?
          break;
        }

        case JVM:
          printlnHeader(writer, "JVM");
          println(writer, "Java Virtual Machine", runtime.getVmName() + " version " + runtime.getVmVersion());
          println(writer, "Vendor", runtime.getVmVendor());
          println(writer, "Uptime", printDuration(runtime.getUptime()));
          try {
            println(writer, "Process CPU time", printDuration(getSunOsValueAsLong(os, "getProcessCpuTime") / 1000000));
          }
          catch (Throwable t) {
            // ignore
          }
          println(writer, "Total compile time",
              printDuration(ManagementFactory.getCompilationMXBean().getTotalCompilationTime()));
          break;

        case THREADS:
          printlnHeader(writer, "Threads");
          println(writer, "Live threads", Integer.toString(threads.getThreadCount()));
          println(writer, "Daemon threads", Integer.toString(threads.getDaemonThreadCount()));
          println(writer, "Peak", Integer.toString(threads.getPeakThreadCount()));
          println(writer, "Total started", Long.toString(threads.getTotalStartedThreadCount()));
          break;

        case MEMORY:
          printlnHeader(writer, "Memory");
          println(writer, "Current heap size", printSizeInKb(mem.getHeapMemoryUsage().getUsed()));
          println(writer, "Maximum heap size", printSizeInKb(mem.getHeapMemoryUsage().getMax()));
          println(writer, "Committed heap size", printSizeInKb(mem.getHeapMemoryUsage().getCommitted()));
          println(writer, "Pending objects", Integer.toString(mem.getObjectPendingFinalizationCount()));
          for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            String val = "Name = '" + gc.getName() + "', Collections = " + gc.getCollectionCount() + ", Time = " +
                printDuration(gc.getCollectionTime());
            println(writer, "Garbage collector", val);
          }
          break;

        case CLASSES:
          printlnHeader(writer, "Classes");
          println(writer, "Current classes loaded", printLong(cl.getLoadedClassCount()));
          println(writer, "Total classes loaded", printLong(cl.getTotalLoadedClassCount()));
          println(writer, "Total classes unloaded", printLong(cl.getUnloadedClassCount()));
          break;

        case OS:
          printlnHeader(writer, "Operating system");
          println(writer, "Name", os.getName() + " version " + os.getVersion());
          println(writer, "Architecture", os.getArch());
          println(writer, "Processors", Integer.toString(os.getAvailableProcessors()));
          try {
            println(writer, "Total physical memory", printSizeInKb(getSunOsValueAsLong(os, "getTotalPhysicalMemorySize")));
            println(writer, "Free physical memory", printSizeInKb(getSunOsValueAsLong(os, "getFreePhysicalMemorySize")));
            println(writer, "Committed virtual memory",
                printSizeInKb(getSunOsValueAsLong(os, "getCommittedVirtualMemorySize")));
            println(writer, "Total swap space", printSizeInKb(getSunOsValueAsLong(os, "getTotalSwapSpaceSize")));
            println(writer, "Free swap space", printSizeInKb(getSunOsValueAsLong(os, "getFreeSwapSpaceSize")));
          }
          catch (Throwable t) {
            // ignore
          }
          break;

        case LICENSE:
          License lic = branding.getLicense();
          printlnHeader(writer, "License");
          println(writer, "Name", lic.getName());
          println(writer, "URL", lic.getUrl());
          writer.println("----8<----");
          writer.println(lic.getContent());
          writer.println("---->8----");
          break;
      }
    }

    writer.flush();

    if (pager) {
      TerminalHelper.pageOutput(io.getTerminal(), buff.toString());
    }
    else {
      io.out.println(buff.toString());
    }

    return Result.SUCCESS;
  }

  private void printlnHeader(final PrintWriter writer, final String name) {
    writer.println(ansi().a(INTENSITY_BOLD).fg(GREEN).a(name).reset());
  }

  private long getSunOsValueAsLong(final OperatingSystemMXBean os, final String name) throws Exception {
    Method mth = os.getClass().getMethod(name);
    return (Long) mth.invoke(os);
  }

  private String printLong(final long i) {
    return FMTI.format(i);
  }

  private String printSizeInKb(final double size) {
    return FMTI.format((long) (size / 1024)) + " kbytes";
  }

  private String printDuration(double uptime) {
    uptime /= 1000;
    if (uptime < 60) {
      return FMTD.format(uptime) + " seconds";
    }
    uptime /= 60;
    if (uptime < 60) {
      long minutes = (long) uptime;
      String s = FMTI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
      return s;
    }
    uptime /= 60;
    if (uptime < 24) {
      long hours = (long) uptime;
      long minutes = (long) ((uptime - hours) * 60);
      String s = FMTI.format(hours) + (hours > 1 ? " hours" : " hour");
      if (minutes != 0) {
        s += " " + FMTI.format(minutes) + (minutes > 1 ? " minutes" : "minute");
      }
      return s;
    }
    uptime /= 24;
    long days = (long) uptime;
    long hours = (long) ((uptime - days) * 60);
    String s = FMTI.format(days) + (days > 1 ? " days" : " day");
    if (hours != 0) {
      s += " " + FMTI.format(hours) + (hours > 1 ? " hours" : "hour");
    }
    return s;
  }

  private void println(final PrintWriter writer, final String name, final Object value) {
    writer.println(ansi().a("  ").a(INTENSITY_BOLD).a(name).reset().a(": ").a(value));
  }
}
