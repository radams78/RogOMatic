package model.gamedata

import model.rogue.Screen

/** Interface for a service class that parses inventory screens */
trait IInventoryParser {
  /** Given an inventory screen, return the corresponding inventory.
   * 
   * Throws an [[UnparsableInventoryScreenException]] if screen is not an inventory screen 
   * 
   * @param screen The inventory screen to be parsed
   * @return Inventory displayed in screen */
  def parseInventoryScreen(screen: Screen): Inventory
}

/** Exception thrown if [[IInventoryParser]] is given a screen that is not an inventory screen */
class UnparsableInventoryScreenException(screen: Screen)
  extends Exception("Could not parse inventory screen:\n" + screen)