package gamedata.items

import domain.Domain
import domain.Domain._
import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamedata.{Fact, UsesKnowledge}

/** A stack of potions
 *
 * Invariants:
 *   - implications is monotone
 *   - potion.infer(potion.potionKnowledge) == Right(potion) 
 *   - potion.infer(PotionKnowledge()) == Right(potion) */
// TODO Duplication with Scroll
case class Potion(quantity: Option[Int] = None,
                  colour: Option[Colour] = None,
                  power: Option[PotionPower] = None) extends Item {
  override def implications: Set[Fact] = (colour, power) match {
    case (Some(c), Some(p)) => Set(Fact.PotionKnowledge(c, p))
    case _ => Set()
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

  implicit def usesKnowledge: UsesKnowledge[Potion] = (self: Potion, fact: Fact) => (fact, self.colour, self.power) match {
    case (Fact.PotionKnowledge(_c, _p), Some(c), Some(p)) if (c == _c && p != _p) || (c != _c && p == _p) =>
      Left(s"Incompatible information: $c -> $p and ${_c} -> ${_p}")
    case (Fact.PotionKnowledge(_c, _p), Some(c), None) if c == _c => Right(Potion(self.quantity, Some(c), Some(_p)))
    case (Fact.PotionKnowledge(_c, _p), None, Some(p)) if p == _p => Right(Potion(self.quantity, Some(_c), Some(p)))
    case _ => Right(self)
  }
}