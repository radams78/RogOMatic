package rogue

class Screen(private val lines : Seq[String]) {
  assert(lines.length == Screen.SCREEN_HEIGHT)
  assert(lines.forall(_.length == Screen.SCREEN_WIDTH))

  lazy val firstLine : String = lines.head

  lazy val lastLine : String = lines.last
}

object Screen {
  private val SCREEN_HEIGHT = 24
  private val SCREEN_WIDTH = 80

  def makeScreen(screenContents: String): Screen = new Screen(
    screenContents
      .stripMargin
      .split("\n")
      .padTo(SCREEN_HEIGHT, "")
      .map(_.padTo(SCREEN_WIDTH, ' '))
  )
}