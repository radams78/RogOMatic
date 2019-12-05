package rogue

/** Interface for the Rogue process */
trait IRogue {
  /** Start the game of Rogue */
  def start(): Unit

  def getScreen: String

  def sendKeypress(keyPress: Char): Unit
}
