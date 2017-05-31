import groovy.json.*

def parser = new JsonSlurper()
def obj = parser.parse(new File('xterm256.json'))

println "public enum StyleColor"
println "{"

def iter = obj.iterator()
while (iter.hasNext()) {
    def color = iter.next()
    print "  ${color.name.toLowerCase(Locale.US)}(${color.colorId})"
    if (iter.hasNext()) {
        println(",")
    }
    else {
        println(";")
    }
}

print """
  public final int code;

  private StyleColor(final int code) {
    this.code = code;
  }
"""

println "}"

return null
