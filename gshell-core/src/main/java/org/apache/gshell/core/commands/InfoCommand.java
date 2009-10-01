/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.core.commands;

import org.apache.gshell.Branding;
import org.apache.gshell.VariableNames;
import org.apache.gshell.command.Command;
import org.apache.gshell.command.CommandActionSupport;
import org.apache.gshell.command.CommandContext;
import org.apache.gshell.io.IO;
import org.apache.gshell.util.Strings;

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
import java.util.Locale;

//
// From Apache Felix Karaf
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

    @Override
    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        int maxNameLen = 25;

        Branding branding = context.getShell().getBranding();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();

        //
        // TODO: i18n all this
        //

        io.info(branding.getAboutMessage());
        printValue(io, "Home", maxNameLen, System.getProperty(VariableNames.SHELL_HOME));
        printValue(io, "Version", maxNameLen, branding.getVersion());
        printValue(io, "Display Name", maxNameLen, branding.getDisplayName());
        printValue(io, "Program Name", maxNameLen, branding.getProgramName());
        printValue(io, "Script Extension", maxNameLen, branding.getScriptExtension());
        printValue(io, "Script Home Dir", maxNameLen, branding.getShellHomeDir());
        printValue(io, "Script Context Dir", maxNameLen, branding.getShellContextDir());
        printValue(io, "Script User Home Dir", maxNameLen, branding.getUserHomeDir());
        printValue(io, "Script User Context Dir", maxNameLen, branding.getUserContextDir());

        io.out.println("JVM");
        printValue(io, "Java Virtual Machine", maxNameLen, runtime.getVmName() + " version " + runtime.getVmVersion());
        printValue(io, "Vendor", maxNameLen, runtime.getVmVendor());
        printValue(io, "Uptime", maxNameLen, printDuration(runtime.getUptime()));
        try {
            printValue(io, "Process CPU time", maxNameLen, printDuration(getSunOsValueAsLong(os, "getProcessCpuTime") / 1000000));
        }
        catch (Throwable t) {}
        printValue(io, "Total compile time", maxNameLen, printDuration(ManagementFactory.getCompilationMXBean().getTotalCompilationTime()));

        io.out.println("Threads");
        printValue(io, "Live threads", maxNameLen, Integer.toString(threads.getThreadCount()));
        printValue(io, "Daemon threads", maxNameLen, Integer.toString(threads.getDaemonThreadCount()));
        printValue(io, "Peak", maxNameLen, Integer.toString(threads.getPeakThreadCount()));
        printValue(io, "Total started", maxNameLen, Long.toString(threads.getTotalStartedThreadCount()));

        io.out.println("Memory");
        printValue(io, "Current heap size", maxNameLen, printSizeInKb(mem.getHeapMemoryUsage().getUsed()));
        printValue(io, "Maximum heap size", maxNameLen, printSizeInKb(mem.getHeapMemoryUsage().getMax()));
        printValue(io, "Committed heap size", maxNameLen, printSizeInKb(mem.getHeapMemoryUsage().getCommitted()));
        printValue(io, "Pending objects", maxNameLen, Integer.toString(mem.getObjectPendingFinalizationCount()));
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            String val = "Name = '" + gc.getName() + "', Collections = " + gc.getCollectionCount() + ", Time = " + printDuration(gc.getCollectionTime());
            printValue(io, "Garbage collector", maxNameLen, val);
        }

        io.out.println("Classes");
        printValue(io, "Current classes loaded", maxNameLen, printLong(cl.getLoadedClassCount()));
        printValue(io, "Total classes loaded", maxNameLen, printLong(cl.getTotalLoadedClassCount()));
        printValue(io, "Total classes unloaded", maxNameLen, printLong(cl.getUnloadedClassCount()));

        io.out.println("Operating system");
        printValue(io, "Name", maxNameLen, os.getName() + " version " + os.getVersion());
        printValue(io, "Architecture", maxNameLen, os.getArch());
        printValue(io, "Processors", maxNameLen, Integer.toString(os.getAvailableProcessors()));
        try {
            printValue(io, "Total physical memory", maxNameLen, printSizeInKb(getSunOsValueAsLong(os, "getTotalPhysicalMemorySize")));
            printValue(io, "Free physical memory", maxNameLen, printSizeInKb(getSunOsValueAsLong(os, "getFreePhysicalMemorySize")));
            printValue(io, "Committed virtual memory", maxNameLen, printSizeInKb(getSunOsValueAsLong(os, "getCommittedVirtualMemorySize")));
            printValue(io, "Total swap space", maxNameLen, printSizeInKb(getSunOsValueAsLong(os, "getTotalSwapSpaceSize")));
            printValue(io, "Free swap space", maxNameLen, printSizeInKb(getSunOsValueAsLong(os, "getFreeSwapSpaceSize")));
        }
        catch (Throwable t) {}

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
    
    private String printSizeInKb(double size) {
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

    private void printValue(final IO io, final String name, final int pad, final Object value) {
        io.out.format("  @|bold %s|%s    %s", name, Strings.repeat(" ", pad - name.length()), value).println();
    }
}