package model

import gamedata.Inventory

trait IInventoryObserver {
  def notify(inventory: Inventory): Unit
}
