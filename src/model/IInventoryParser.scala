package model

import model.gamedata.Inventory
import model.rogue.Screen

trait IInventoryParser {
  def parseInventoryScreen(screen: Screen): Inventory
}
