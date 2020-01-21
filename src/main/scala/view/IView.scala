package view

import gamedata.{Fact, pCommand, pInventory}

/** Interface for the IO view. The implementations should be Humble Object */
trait IView {
  def displayFact(fact: Fact): Unit

  /** Get a command from the user */
  def getCommand: pCommand

  /** Display an error message */
  def displayError(s: String): Unit

  /** Display the PC's inventory */
  def displayInventory(inventory: pInventory): Unit

  /** Display the current screen exactly as received from Rogue */
  def displayScreen(screen: String): Unit

  /** Display the game over message */
  def displayGameOver(finalScore: Int): Unit
}
