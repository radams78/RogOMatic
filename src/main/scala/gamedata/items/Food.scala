package gamedata.items

import domain.Domain._

/** A stack of rations of food */
case class Food(quantity: Int) extends Item {
  override def toString: String = s"$quantity food"

  override def merge(that: Item): Either[String, Item] = that match {
    case Food(thatQuantity) =>
      for {inferredQuantity <- quantity.merge(thatQuantity)} yield Food(inferredQuantity)
    case _ => Left(s"Incompatible items: $this and $that")
  }
}
