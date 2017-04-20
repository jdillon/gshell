package com.planet57.gshell.util;

import java.util.Locale;

/**
 * Operation-system helpers.
 */
public class OperatingSystem
{
  public static final String NAME = System.getProperty("os.name").toLowerCase(Locale.US);

  public static final boolean WINDOWS = NAME.contains("windows");
}
