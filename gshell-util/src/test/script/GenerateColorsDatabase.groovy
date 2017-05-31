@Grab('com.google.guava:guava:20.0')

/**
 * Guess colors from helper.
 *
 * @see: https://github.com/tweekmonster/shellcolors
 */
class GenerateColorsDatabase
{
  static void main(String[] args) {
    def dir = new File(this.protectionDomain.codeSource.location.file).parentFile
    def script = new File(dir, 'shellcolors')

    println 'class ColorsDatabase'
    println '{'

    def generateMap = { String fname, Map<String,String> input ->
      println "  static Map<String,Object> $fname = ["
      input.each { hex, name ->
        String code = "${script.absolutePath} -q $hex".execute().text.trim()
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


