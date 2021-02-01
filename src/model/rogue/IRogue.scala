package model.rogue

/** Interface for the Rogue process. */
trait IRogue {
  /** Retrieve the current contents of the screen.
   * 
   * The screen contents are returned as a string with lines separated by \n's.
   * 
   * @return Current contents of the screen */
  def getScreenContents: String

  /** Send a keypress to model.rogue.Rogue as if the player had pressed it.
   *
   * Throws a [[GameNotStartedException]] if startGame has not yet been called.
   *
   * @param keypress Keypress to be sent to model.rogue.Rogue */
  def sendKeypress(keypress: Char): Unit

  /** Start the model.rogue.Rogue process
   *
   * Should be called before any keypress is sent to model.rogue.Rogue. Throws a [[GameAlreadyStartedException]] if startGame is called twice. */
  def startGame() : Unit
}

/** Exception thrown if we try to send a keypress to model.rogue.Rogue before the game is started */
class GameNotStartedException extends Exception

/** Exception thrown if we try to start the game twice */
class GameAlreadyStartedException extends Exception