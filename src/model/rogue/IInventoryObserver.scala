package model.rogue

import model.items.Inventory

trait IInventoryObserver {
  def notify(inventory: Inventory): Unit
}
