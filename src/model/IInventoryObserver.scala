package model

import model.gamedata.Inventory

trait IInventoryObserver {
  def notify(inventory: Inventory): Unit
}
