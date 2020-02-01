package view

import gamedata.fact.Fact
import gamestate.Inventory
import rogue.Command

/** Interface for the IO view. The implementations should be Humble Object */
trait IView {
  /** Get a command from the user */
  def getCommand: Command

  /** Display an error message */
  def displayError(s: String): Unit

  /** Display a fact that has been learned */
  def displayFact(fact: Fact): Unit

  /** Display the PC's inventory */
  def displayInventory(inventory: Inventory): Unit

  /** Display the current screen exactly as received from Rogue */
  def displayScreen(screen: String): Unit

  /** Display the game over message */
  def displayGameOver(finalScore: Int): Unit
}
