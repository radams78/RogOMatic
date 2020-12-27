package rogue

/** A screen displyed by the Rogue process
 *
 * A screen must consist of 24 lines of length 80. If [[lines]] does not fit this, the constructor throws an
 * [[AssertionError]].
 *
 * @param lines The content of the screen. Must be a sequence of 24 lines of length 80. */
class Screen(private val lines : Seq[String]) {
  assert(lines.length == Screen.SCREEN_HEIGHT)
  assert(lines.forall(_.length == Screen.SCREEN_WIDTH))

  /** The top line on the screen */
  lazy val firstLine : String = lines.head

  /** The bottom line on the screen */
  lazy val lastLine : String = lines.last

  override def equals(obj: Any): Boolean = obj match {
    case screen : Screen => lines == screen.lines
    case _ => false
  }
}

object Screen {
  private val SCREEN_HEIGHT = 24
  private val SCREEN_WIDTH = 80

  /** Create a [[Screen]] from the given string.
   *
   * The string is split into lines at each \n, then padded with spaces to make 24 lines of length 80. The method throws
   * an [[AssertionError]] if screenContents consists of more than 24 lines, or if any line has length > 80.
   *
   * @param screenContents The contents of the screen as a single string, with lines separated by \n */
  def makeScreen(screenContents: String): Screen = new Screen(
    screenContents
      .stripMargin
      .split("\n")
      .padTo(SCREEN_HEIGHT, "")
      .map(_.padTo(SCREEN_WIDTH, ' '))
  )
}