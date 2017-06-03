/**
 * Generate StyleColor enum.
 */
class GenerateStyleColor
{
  static void main(String[] args) {

    println "public enum StyleColor"
    println "{"

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
      println(" // #${color.hex}")
    }

    print """
  public final int code;

  private StyleColor(final int code) {
    this.code = code;
  }
"""

    println "}"
  }
}
