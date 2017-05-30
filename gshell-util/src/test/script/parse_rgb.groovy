// parse X11 rgb.txt

@Grab('com.google.guava:guava:20.0')

import com.google.common.collect.*

def file = new File('rgb.txt')

@groovy.transform.ToString
class Color
{
  int hash

  int red

  int green

  int blue

  String name

  String hex

  Color(String line) {
    def parts = line.trim().split('\\s+', 4)
    this.red = parts[0] as int
    this.green = parts[1] as int
    this.blue = parts[2] as int
    this.name = parts[3]
    this.hex = String.format("%02x%02x%02x", red, green, blue)
    this.hash = Integer.parseInt(this.hex, 16)
  }

  String rgb() {
    return String.format("%03d/%03d/%03d", red, green, blue)
  }

  @Override
  int hashCode() {
    return hash
  }
}

def colors = HashMultimap.create()
def names = [] as HashSet

file.eachLine { line ->
  if (!line.startsWith('!')) {
    def color = new Color(line)
    colors.put(color.hex, color)
    if (names.contains(color.name)) {
      println "Duplicate color name: $color"
    }
    names.add(color.name)
  }
}

println "${colors.size()} colors:"
colors.asMap().each { key, values ->
  println "${key} ${values[0].rgb()} ${values.collect { it.name }}"
}

println()
println "${names.size()} color-names:"
names.sort().each {
  println it
}
