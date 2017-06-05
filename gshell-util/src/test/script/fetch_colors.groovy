@Grab('org.jsoup:jsoup:1.10.2')

import org.jsoup.*

ColorSchemes.svg.each { hex, name ->
  def url = new URL("http://www.htmlcsscolor.com/hex/$hex")
  def doc = Jsoup.parse(url.text)
  println "$hex: ${doc.title()}"
  Thread.sleep(500)
}
