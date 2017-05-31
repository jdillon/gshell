@Grab('com.google.guava:guava:20.0')

/**
 * Guess colors from helper.
 *
 * @see: https://github.com/tweekmonster/shellcolors
 */
class GenerateColorsDatabase
{
  static final File script
  static {
    def dir = new File(this.protectionDomain.codeSource.location.file).parentFile
    script = new File(dir, 'shellcolors')
  }

  static int codeForHex(String hex) {
    String code = "${script.absolutePath} -q $hex".execute().text.trim()
    return Integer.parseInt(code)
  }

  static void main(String[] args) {
    println 'class ColorsDatabase'
    println '{'

    def generateMap = { String fname, Map<String,String> input ->
      println "  static List<Map> $fname = ["
      input.each { hex, name ->
        int code = codeForHex(hex)
        println "    [ hex: '$hex', name: '$name', code: $code ],"
      }
      println '  ]'
    }

    generateMap 'x11', ColorSchemes.x11
    println()
    generateMap 'svg', ColorSchemes.svg

    println '}'
  }
}


