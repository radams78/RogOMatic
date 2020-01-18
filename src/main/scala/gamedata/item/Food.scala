package gamedata.item

import domain.Domain._

/** A stack of rations of food */
case class Food(quantity: Int) extends pItem {
  override def toString: String = s"$quantity food"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case Food(thatQuantity) =>
      for {inferredQuantity <- quantity.merge(thatQuantity)} yield Food(inferredQuantity)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}
