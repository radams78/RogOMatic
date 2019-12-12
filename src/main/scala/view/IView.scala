package view

import gamedata.Inventory

/** Interface for the View component of the MVC architecture. Handles user IO. */
trait IView {
  def displayGameOver(score: Int): Unit

  /** Display an error message */
  def displayError(s: String): Unit

  /** Display the PC's inventory */
  def displayInventory(inventory: Inventory): Unit

  /** Display the current screen exactly as received from Rogue */
  def displayScreen(screen: String): Unit
}
