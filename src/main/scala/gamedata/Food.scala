package gamedata

/** A stack of rations of food */
case class Food(quantity: Int) extends Item {
  override def toString: String = s"$quantity food"
}
