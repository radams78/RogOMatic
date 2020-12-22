package services

object ScreenFactory {
  def makeScreen(screenContents: String): Seq[String] = {
    screenContents
      .stripMargin
      .split("\n")
      .padTo(24, "")
      .map(_.padTo(80, ' '))
  }
}
