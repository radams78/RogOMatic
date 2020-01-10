package gamedata.items

import domain.Domain
import domain.Domain._
import gamedata.ProvidesKnowledge
import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamestate.PotionKnowledge

/** A stack of potions
 *
 * Invariants:
 *   - potionKnowledge is monotone
 *   - potion.infer(potion.potionKnowledge) == Right(potion) 
 *   - potion.infer(PotionKnowledge()) == Right(potion) */
// TODO Duplication with Scroll
case class Potion(quantity: Option[Int] = None,
                  colour: Option[Colour] = None,
                  power: Option[PotionPower] = None) extends Item {
  override def potionKnowledge: Either[String, PotionKnowledge] = Right((colour, power) match {
    case (Some(c), Some(p)) => PotionKnowledge(Map(c -> p))
    case _ => PotionKnowledge()
  })

  override def infer(that: ProvidesKnowledge): Either[String, Potion] = for {
    pk <- that.potionKnowledge
    potion <- infer(pk)
  } yield potion

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
      })

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
  val UNKNOWN: Potion = Potion()

  def apply(power: PotionPower): Potion = Potion(None, None, Some(power))

  def apply(quantity: Int, colour: Colour): Potion = Potion(Some(quantity), Some(colour), None)

  implicit def domain: Domain[Potion] = (x: Potion, y: Potion) => x.merge(y)
}