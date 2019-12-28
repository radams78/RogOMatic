package gamedata

import gamedata.Colour.Colour
import gamedata.Domain._
import gamedata.PotionPower.PotionPower

/** A stack of potions */
// TODO Duplication with Scroll
case class Potion(quantity: Option[Int] = None,
                  colour: Option[Colour] = None,
                  power: Option[PotionPower] = None) extends Item {
  def infer(potionKnowledge: PotionKnowledge): Either[String, Potion] = (colour, power) match {
    case (Some(c), power) => for (_power <- potionKnowledge.getPower(c).merge(power)) yield Potion(quantity, colour, _power)
    case (None, Some(p)) => Right(Potion(quantity, potionKnowledge.getColour(p), Some(p)))
    case _ => Right(this)
  }

  override def toString: String =
    (quantity match {
      case Some(q) => q.toString
      case None => "some"
    }) +
      (colour match {
        case Some(c) => " " + c.toString
        case None => ""
      }) +
      (if (quantity.contains(1)) " potion" else " potions") +
      (power match {
        case Some(p) => " " + p.toString
        case None => ""
      }) // TODO Duplication

  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Potion(thatQuantity, thatColour, thatPower) => for {
      inferredQuantity <- quantity.merge(thatQuantity)
      inferredColour <- colour.merge(thatColour)
      inferredPower <- power.merge(thatPower)
    } yield Potion(inferredQuantity, inferredColour, inferredPower).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }
}

object Potion {
  def apply(quantity: Int, colour: Colour): Potion = Potion(Some(quantity), Some(colour), None)

  implicit def domain: Domain[Potion] = (x: Potion, y: Potion) => x.merge(y)
}