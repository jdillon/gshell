@Grab('com.google.guava:guava:20.0')

import com.google.common.collect.Multimap
import com.google.common.collect.HashMultimap

/**
 * Display xterm-256 color pallet, with ANSI color codes and RGB.
 *
 * @see: https://en.wikipedia.org/wiki/Xterm
 * @see: https://github.com/zhengkai/config/blob/master/script/256colors2.pl
 */
class DisplayColors
{
  static Multimap<Integer,String> codeNames = HashMultimap.create()

  static Multimap<String,String> hexNames = HashMultimap.create()

  static int unknownMapping = 0

  static int duplicateNames = 0

  static Set<String> uniqueNames = []

  static {
    ColorsDatabase.x11.each { Map color ->
      codeNames.put(color.code, color.name)
      hexNames.put(color.hex, color.name)
    }
    ColorsDatabase.svg.each { Map color ->
      codeNames.put(color.code, color.name)
      hexNames.put(color.hex, color.name)
    }

//    def dir = new File(this.protectionDomain.codeSource.location.file).parentFile
//    new RgbParser().parse(new File(dir, 'rgb.txt')).asMap().each { hex, values ->
//      values.each {
//        hexNames.put(hex, it.name)
//        int code = GenerateColorsDatabase.codeForHex(hex)
//        codeNames.put(code, it.name)
//      }
//    }
  }

  static String selectName(int code, String hex) {
    Collection names = codeNames.get(code)
    if (names == null || names.empty) {
      names = hexNames.get(hex)
    }

    String name
    if (names != null && !names.empty) {
      name = names[0]
    }
    else {
      unknownMapping++
      name = ColorSchemes.xterm256[hex].toLowerCase(Locale.US)
    }

    if (uniqueNames.contains(name)) {
      duplicateNames++
    }
    uniqueNames.add(name)
    return name
  }

  static void main(String[] args) {
    println 'System colors (0-15):'
    List<String> system = [
        '000000', // 000
        '800000', // 001
        '008000', // 002
        '808000', // 003
        '000080', // 004
        '800080', // 005
        '008080', // 006
        'c0c0c0', // 007
        // bright
        '808080', // 008
        'ff0000', // 009
        '00ff00', // 010
        'ffff00', // 011
        '0000ff', // 012
        'ff00ff', // 013
        '00ffff', // 014
        'ffffff'  // 015
    ]
    for (int color = 0; color < 8; color++) {
      String hex = system[color]
      def name = selectName(color, hex)
      print "\033[48;5;${color}m ${String.format('%3s %s %12s', color, hex, name)} \033[0m "
    }
    println()

    // bright
    for (int color = 8; color < 16; color++) {
      String hex = system[color]
      def name = selectName(color, hex)
      print "\033[38;5;0m" // darker fg
      print "\033[48;5;${color}m ${String.format('%3s %s %12s', color, hex, name)} \033[0m "
    }
    println()

    println()

    println 'Color cube, 6x6x6 (16-231):'
    for (int green = 0; green < 6; green++) {
      for (int red = 0; red < 6; red++) {
        for (int blue = 0; blue < 6; blue++) {
          int color = 16 + (red * 36) + (green * 6) + blue
          int r = (red ? (red * 40 + 55) : 0)
          int g = (green ? (green * 40 + 55) : 0)
          int b = (blue ? (blue * 40 + 55) : 0)
          String hex = String.format('%02x%02x%02x', r, g, b)
          def name = selectName(color, hex)

          if (green > 2) {
            print "\033[38;5;0m" // darker fg
          }
          print "\033[48;5;${color}m ${String.format('%3s %s %17s', color, hex, name)} \033[0m "
        }
        println()
      }
    }
    println()

    println 'Grayscale ramp (232-255):'
    for (int gray = 0; gray < 24; gray++) {
      int color = gray + 232
      int level = (gray * 10) + 8
      int r = level
      int g = level
      int b = level
      String hex = String.format('%02x%02x%02x', r, g, b)
      def name = selectName(color, hex)

      if (gray > 11) {
        print "\033[38;5;0m" // darker fg
      }
      print "\033[48;5;${color}m ${String.format('%3s %s %10s', color, hex, name)} \033[0m\n"
    }

    println()
    println "Unknown color name mappings: $unknownMapping"
    println "Duplicate colors names: $duplicateNames"
  }
}


