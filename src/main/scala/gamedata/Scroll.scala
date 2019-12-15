package gamedata

/** A stack of scrolls */
case class Scroll(quantity: Int, title: String) extends Item {
  override def toString: String = s"a scroll entitled: '$title'"
}
