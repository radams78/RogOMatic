package gamedata

import gamedata.Domain._

/** A stack of rations of food */
case class Food(quantity: Int) extends Item {
  override def toString: String = s"$quantity food"

  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Food(thatQuantity) =>
      for {inferredQuantity <- quantity.merge(thatQuantity)} yield Food(inferredQuantity).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }
}
