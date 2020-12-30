package model.items

import model.rogue.Screen

object InventoryParser extends IInventoryParser {
  override def parseInventoryScreen(screen: Screen): Inventory = Inventory(
    Map(
      Slot.A -> Food,
      Slot.B -> RingMail(+1),
      Slot.C -> Mace(+1, +1),
      Slot.D -> ShortBow(+1, +0),
      Slot.E -> Arrows(32, +0, +0)
    ),
    wearing = Slot.B,
    wielding = Slot.C
  )
}
