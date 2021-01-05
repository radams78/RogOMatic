package model.items

case class Inventory(items : Map[Slot, Item] = Map(),
                     wearing : Option[Slot] = None, 
                     wielding: Option[Slot] = None)

object Inventory {
  def apply(items : Map[Slot, Item], wearing : Slot, wielding : Slot) : Inventory = Inventory(items, Some(wearing), Some(wielding))
}
