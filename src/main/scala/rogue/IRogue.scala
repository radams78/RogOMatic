package rogue

/** Interface for the Rogue process */
trait IRogue {
  /** Close the Rogue process */
  def close(): Unit

  /** Start the game of Rogue */
  def start(): Unit

  /** The current screen being displayed by Rogue */
  def getScreen: String

  /** Send a character to Rogue as if from the user */
  def sendKeypress(keyPress: Char): Unit
}
