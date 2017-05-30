// Display xterm-256 color pallet, with ANSI color codes and RGB

// see: https://en.wikipedia.org/wiki/Xterm
// see: https://github.com/zhengkai/config/blob/master/script/256colors2.pl

print "System colors (0-15):\n"
def system = [
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
  print "\033[48;5;${color}m ${String.format("%3s %s", color, system[color])} \033[0m "
}
println()

// bright
for (int color = 8; color < 16; color++) {
  print "\033[48;5;${color}m ${String.format("%3s %s", color, system[color])} \033[0m "
}
println()

println()

print "Color cube, 6x6x6 (16-231):\n"
for (int green = 0; green < 6; green++) {
  for (int red = 0; red < 6; red++) {
    for (int blue = 0; blue < 6; blue++) {
      int color = 16 + (red * 36) + (green * 6) + blue
      int r = (red ? (red * 40 + 55) : 0)
      int g = (green ? (green * 40 + 55) : 0)
      int b = (blue ? (blue * 40 + 55) : 0)
      print "\033[48;5;${color}m ${String.format("%3s %02x%02x%02x", color, r, g, b)} \033[0m "
    }
    println()
  }
}
println()

print "Grayscale ramp (232-255):\n"
for (int gray = 0; gray < 24; gray++) {
  int color = gray + 232
  int level = (gray * 10) + 8
  int r = level
  int g = level
  int b = level
  print "\033[48;5;${color}m ${String.format("%3s %02x%02x%02x", color, r, g, b)} \033[0m\n"
}
