/**
 * Generate StyleColor enum.
 */
class GenerateStyleColor
{
  static void main(String[] args) {

    println """
package com.planet57.gshell.util.style;

/**
 * Style colors.
 *
 * @since 3.0
 */
public enum StyleColor
{"""
    def iter = ColorsDatabase.xterm256.iterator()
    while (iter.hasNext()) {
      Map color = iter.next()
      print "  ${color.name}(${color.code})"
      if (iter.hasNext()) {
        print(",")
      }
      else {
        print(";")
      }

      // include comment for aprox color hex code; but don't encode into class
      println(" // #${color.hex}")
    }

    print """
  public final int code;

  StyleColor(final int code) {
    this.code = code;
  }
"""

    println "}"
  }
}
