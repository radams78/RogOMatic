package view

import gamedata.Inventory

/** A simple text input/output */
class TextView extends IView {
  override def displayScreen(screen: String): Unit = println(screen)

  override def displayInventory(inventory: Inventory): Unit = {
    for ((slot, item) <- inventory.items.toList.sortBy(_._1)) println(s"$slot) $item")
    for (slot <- inventory.wielding) println(s"WEAPON: $slot) ${inventory.items(slot)}")
    for (slot <- inventory.wearing) println(s"ARMOR: $slot) ${inventory.items(slot)}")
  }
}
