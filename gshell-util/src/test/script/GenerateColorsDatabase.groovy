import groovy.json.JsonSlurper

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

    generateSystem()
    println()
    generateAnsi()
    println()
    generateXterm256()
    println()
    generateBestMatch('x11', ColorSchemes.x11)
    println()
    generateBestMatch('svg', ColorSchemes.svg)

    println '}'
  }

  private static void generateSystem() {
    println """  static List<Map> system = [
    [ hex: '000000', name: 'black', code: 0 ],
    [ hex: '800000', name: 'maroon', code: 1 ],
    [ hex: '008000', name: 'green', code: 2 ],
    [ hex: '808000', name: 'olive', code: 3 ],
    [ hex: '000080', name: 'navy', code: 4 ],
    [ hex: '800080', name: 'purple', code: 5 ],
    [ hex: '008080', name: 'teal', code: 6 ],
    [ hex: 'c0c0c0', name: 'silver', code: 7 ],
    [ hex: '808080', name: 'grey', code: 8 ],
    [ hex: 'ff0000', name: 'red', code: 9 ],
    [ hex: '00ff00', name: 'lime', code: 10 ],
    [ hex: 'ffff00', name: 'yellow', code: 11 ],
    [ hex: '0000ff', name: 'blue', code: 12 ],
    [ hex: 'ff00ff', name: 'fuchsia', code: 13 ],
    [ hex: '00ffff', name: 'aqua', code: 14 ],
    [ hex: 'ffffff', name: 'white', code: 15 ]
  ]"""
  }

  private static void generateAnsi() {
    println """  static List<Map> ansi = [
    [ hex: '000000', name: 'black', code: 0 ],
    [ hex: '800000', name: 'red', code: 1 ],
    [ hex: '008000', name: 'green', code: 2 ],
    [ hex: '808000', name: 'yellow', code: 3 ],
    [ hex: '000080', name: 'blue', code: 4 ],
    [ hex: '800080', name: 'magenta', code: 5 ],
    [ hex: '008080', name: 'cyan', code: 6 ],
    [ hex: 'c0c0c0', name: 'white', code: 7 ],
    [ hex: '808080', name: 'bright-black', code: 8 ],
    [ hex: 'ff0000', name: 'bright-red', code: 9 ],
    [ hex: '00ff00', name: 'bright-green', code: 10 ],
    [ hex: 'ffff00', name: 'bright-yellow', code: 11 ],
    [ hex: '0000ff', name: 'bright-blue', code: 12 ],
    [ hex: 'ff00ff', name: 'bright-magenta', code: 13 ],
    [ hex: '00ffff', name: 'bright-cyan', code: 14 ],
    [ hex: 'ffffff', name: 'bright-white', code: 15 ]
  ]"""
  }

  static class UniqueNames
  {
    Map<String,Integer> nameCounts = [:]

    String get(String name) {
      def count = nameCounts[name]
      if (count == null) {
        count = 0
      }
      else {
        ++count
      }
      nameCounts[name] = count
      if (count != 0) {
        name = "${name}${count + 96 as char}" // suffix a..z
      }
      return name
    }
  }

  private static void generateXterm256() {
    def parser = new JsonSlurper()
    def dir = new File(this.protectionDomain.codeSource.location.file).parentFile

    def obj = parser.parse(new File(dir, 'xterm256.json'))

    def uniqueNames = new UniqueNames()

    println '  static List<Map> xterm256 = ['
    def iter = obj.iterator()
    while (iter.hasNext()) {
      def it = iter.next()

      // make unique names
      def name = uniqueNames.get(it.name.toLowerCase(Locale.US))

      print "    [ hex: '${it.hexString[1..-1]}', name: '$name', code: ${it.colorId} ]"
      if (iter.hasNext()) {
        print ","
      }
      println()
    }
    println '  ]'
  }

  private static void generateBestMatch(String fname, Map<String,String> input) {
    def uniqueNames = new UniqueNames()

    println "  static List<Map> $fname = ["
    def iter = input.entrySet().iterator()
    while (iter.hasNext()) {
      def entry = iter.next()
      def hex = entry.key
      int code = codeForHex(hex)

      // make unique names
      def name = uniqueNames.get(entry.value.toLowerCase(Locale.US))

      print "    [ hex: '$hex', name: '${name}', code: $code ]"
      if (iter.hasNext()) {
        print ","
      }
      println()
    }
    println '  ]'
  }
}


