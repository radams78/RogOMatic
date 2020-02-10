package gamedata.item

import gamedata.Slot
import gamedata.fact.Fact

/** A fact that gives the contents of an inventory slot */
case class InSlot(slot: Slot, item: Option[pItem]) extends Fact
