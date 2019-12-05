package gamedata

/** The PC's inventory */
case class Inventory(items: Map[Slot, Item] = Map(),
                     wearing: Option[Slot] = None,
                     wielding: Option[Slot] = None)
