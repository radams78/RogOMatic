package view

import gamedata.Inventory
import rogue.Command

/** Interface for the IO view. The implementations should be Humble Object */
trait IView {
  def getCommand: Command

  /** Display an error message */
  def displayError(s: String): Unit

  /** Display the PC's inventory */
  def displayInventory(inventory: Inventory): Unit

  /** Display the current screen exactly as received from Rogue */
  def displayScreen(screen: String): Unit

  /** Display the game over message */
  def displayGameOver(finalScore: Int): Unit
}
