package view

import gamedata.Inventory

/** Interface for the View component of the MVC architecture. Handles user IO. */
trait IView {
  def displayInventory(inventory: Inventory): Unit


  def displayScreen(screen: String): Unit
}
