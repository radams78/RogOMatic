package gamedata

import gamedata.Colour.Colour

/** A stack of potions */
case class Potion(quantity: Int, colour: Colour) extends Item {
  override def toString: String = s"$quantity $colour potion" + (if (quantity > 1) "s" else "")
}
