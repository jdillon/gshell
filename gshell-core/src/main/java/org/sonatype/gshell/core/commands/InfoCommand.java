/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.core.commands;

import jline.Terminal;
import jline.WindowsTerminal;
import org.sonatype.gshell.Branding;
import org.sonatype.gshell.ansi.Ansi;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.cli.Option;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.core.command.CommandActionSupport;
import static org.sonatype.gshell.core.commands.InfoCommand.Section.*;

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
import java.util.List;
import java.util.Locale;

//
// Based on info command from Apache Felix
//

/**
 * Display shell information.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
@Command
public class InfoCommand
    extends CommandActionSupport
{
    private static final NumberFormat FMTI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));

    private static final NumberFormat FMTD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

    public static enum Section
    {
        SHELL,
        TERMINAL,
        JVM,
        THREADS,
        MEMORY,
        CLASSES,
        OS,
    }

    @Argument(multiValued=true)
    private List<Section> sections;

    @Option(name="-a", aliases={ "--all" })
    private boolean all;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
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
            sections = Arrays.asList(SHELL);
        }
        
        //
        // TODO: i18n all this
        //

        for (Section section : sections) {
            switch (section) {
                case SHELL:
                    io.info("Shell");
                    println(io, "Display Name", branding.getDisplayName());
                    println(io, "Program Name", branding.getProgramName());
                    println(io, "Version", branding.getVersion());
                    println(io, "Home Dir", branding.getShellHomeDir());
                    println(io, "Context Dir", branding.getShellContextDir());
                    println(io, "User Home Dir", branding.getUserHomeDir());
                    println(io, "User Context Dir", branding.getUserContextDir());
                    println(io, "Script Extension", branding.getScriptExtension());
                    println(io, "ANSI", Ansi.isEnabled());
                    break;

                case TERMINAL:
                    io.out.println("Terminal");
                    Terminal term = io.getTerminal();
                    println(io, "Type", term.getClass().getName());
                    println(io, "Supported", term.isSupported());
                    println(io, "Height", term.getHeight());
                    println(io, "Width", term.getWidth());
                    println(io, "ANSI", term.isAnsiSupported());
                    println(io, "Echo", term.isEchoEnabled());
                    if (term instanceof WindowsTerminal) {
                        println(io, "Direct Console", ((WindowsTerminal)term).getDirectConsole());
                    }
                    break;

                case JVM:
                    io.out.println("JVM");
                    println(io, "Java Virtual Machine", runtime.getVmName() + " version " + runtime.getVmVersion());
                    println(io, "Vendor", runtime.getVmVendor());
                    println(io, "Uptime", printDuration(runtime.getUptime()));
                    try {
                        println(io, "Process CPU time", printDuration(getSunOsValueAsLong(os, "getProcessCpuTime") / 1000000));
                    }
                    catch (Throwable t) {}
                    println(io, "Total compile time", printDuration(ManagementFactory.getCompilationMXBean().getTotalCompilationTime()));
                    break;

                case THREADS:
                    io.out.println("Threads");
                    println(io, "Live threads", Integer.toString(threads.getThreadCount()));
                    println(io, "Daemon threads", Integer.toString(threads.getDaemonThreadCount()));
                    println(io, "Peak", Integer.toString(threads.getPeakThreadCount()));
                    println(io, "Total started", Long.toString(threads.getTotalStartedThreadCount()));
                    break;
                
                case MEMORY:
                    io.out.println("Memory");
                    println(io, "Current heap size", printSizeInKb(mem.getHeapMemoryUsage().getUsed()));
                    println(io, "Maximum heap size", printSizeInKb(mem.getHeapMemoryUsage().getMax()));
                    println(io, "Committed heap size", printSizeInKb(mem.getHeapMemoryUsage().getCommitted()));
                    println(io, "Pending objects", Integer.toString(mem.getObjectPendingFinalizationCount()));
                    for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                        String val = "Name = '" + gc.getName() + "', Collections = " + gc.getCollectionCount() + ", Time = " + printDuration(gc.getCollectionTime());
                        println(io, "Garbage collector", val);
                    }
                    break;

                case CLASSES:
                    io.out.println("Classes");
                    println(io, "Current classes loaded", printLong(cl.getLoadedClassCount()));
                    println(io, "Total classes loaded", printLong(cl.getTotalLoadedClassCount()));
                    println(io, "Total classes unloaded", printLong(cl.getUnloadedClassCount()));
                    break;

                case OS:
                    io.out.println("Operating system");
                    println(io, "Name", os.getName() + " version " + os.getVersion());
                    println(io, "Architecture", os.getArch());
                    println(io, "Processors", Integer.toString(os.getAvailableProcessors()));
                    try {
                        println(io, "Total physical memory", printSizeInKb(getSunOsValueAsLong(os, "getTotalPhysicalMemorySize")));
                        println(io, "Free physical memory", printSizeInKb(getSunOsValueAsLong(os, "getFreePhysicalMemorySize")));
                        println(io, "Committed virtual memory", printSizeInKb(getSunOsValueAsLong(os, "getCommittedVirtualMemorySize")));
                        println(io, "Total swap space", printSizeInKb(getSunOsValueAsLong(os, "getTotalSwapSpaceSize")));
                        println(io, "Free swap space", printSizeInKb(getSunOsValueAsLong(os, "getFreeSwapSpaceSize")));
                    }
                    catch (Throwable t) {}
                    break;
            }
        }
        
        return Result.SUCCESS;
    }

    private long getSunOsValueAsLong(OperatingSystemMXBean os, String name) throws Exception {
        Method mth = os.getClass().getMethod(name);
        return (Long) mth.invoke(os);
    }

    private String printLong(long i) {
        return FMTI.format(i);
    }

    //
    // TODO: i18n all this
    //
    
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

    private void println(final IO io, final String name, final Object value) {
        io.out.format("  @|bold %s|: %s", name, value).println();
    }
}