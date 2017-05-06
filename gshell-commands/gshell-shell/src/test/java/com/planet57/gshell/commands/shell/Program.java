package com.planet57.gshell.commands.shell;

import java.util.Arrays;

/**
 * Helper program for testing {@link JavaAction}.
 */
public class Program
{
  public static void main(final String[] args) {
    System.out.println("test: " + Arrays.asList(args));
  }

  public static int returnsValue(final String[] args) {
    System.out.println("test: " + Arrays.asList(args));
    return 57;
  }
}
