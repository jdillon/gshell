import java.util.regex.Pattern

class ParseColors
{
  static void main(String[] args) {
    def pattern = Pattern.compile('^.*HEX color #([^,]+), Color name: ([^,]+)')

    def dir = new File(this.protectionDomain.codeSource.location.file).parentFile
    def file = new File(dir, 'xterm256-htmlcsscolors.txt')
    file.eachLine { line ->
      def matcher = pattern.matcher(line)
      println matcher.group(1)
      println matcher.group(2)
    }
  }
}
