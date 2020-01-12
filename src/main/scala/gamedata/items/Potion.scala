package gamedata.items

import domain.Domain
import domain.Domain._
import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamedata.{Fact, UsesKnowledge}

trait MagicItem[A, P] extends Item {
  def quantity: Option[Int]

  def attribute: Option[A]

  def power: Option[P]

  def singular: String

  def plural: String

  override def implications: Set[Fact] = (attribute, power) match {
    case (Some(a), Some(p)) => Set(Fact.MagicItemKnowledge(a, p))
    case _ => Set()
  }

  override def toString: String =
    (quantity match {
      case Some(q) => q.toString
      case None => "some"
    }) +
      (attribute match {
        case Some(a) => " " + a.toString
        case None => ""
      }) + " " +
      (if (quantity.contains(1)) singular else plural) +
      (power match {
        case Some(p) => " " + p.toString
        case None => ""
      })

}

/** A stack of potions
 *
 * Invariants:
 *   - implications is monotone
 *   - potion.infer(potion.potionKnowledge) == Right(potion) 
 *   - potion.infer(PotionKnowledge()) == Right(potion) */
// TODO Duplication with Scroll
case class Potion(quantity: Option[Int] = None,
                  colour: Option[Colour] = None,
                  power: Option[PotionPower] = None) extends MagicItem[Colour, PotionPower] {
  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Potion(thatQuantity, thatColour, thatPower) => for {
      inferredQuantity <- quantity.merge(thatQuantity)
      inferredColour <- colour.merge(thatColour)
      inferredPower <- power.merge(thatPower)
    } yield Potion(inferredQuantity, inferredColour, inferredPower).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }

  override def attribute: Option[Colour] = colour

  override def singular: String = "potion"

  override def plural: String = "potions"
}

object Potion {
  val UNKNOWN: Potion = Potion()

  def apply(power: PotionPower): Potion = Potion(None, None, Some(power))

  def apply(quantity: Int, colour: Colour): Potion = Potion(Some(quantity), Some(colour), None)

  implicit def domain: Domain[Potion] = (x: Potion, y: Potion) => x.merge(y)

  implicit def usesKnowledge: UsesKnowledge[Potion] = (self: Potion, fact: Fact) => (fact, self.colour, self.power) match {
    case (Fact.MagicItemKnowledge(_c, _p), Some(c), Some(p)) if (c == _c && p != _p) || (c != _c && p == _p) =>
      Left(s"Incompatible information: $c -> $p and ${_c} -> ${_p}")
    case (Fact.MagicItemKnowledge(_c, _p: PotionPower), Some(c), None) if c == _c => Right(Potion(self.quantity, Some(c), Some(_p)))
    case (Fact.MagicItemKnowledge(_c: Colour, _p), None, Some(p)) if p == _p => Right(Potion(self.quantity, Some(_c), Some(p)))
    case _ => Right(self)
  }
}