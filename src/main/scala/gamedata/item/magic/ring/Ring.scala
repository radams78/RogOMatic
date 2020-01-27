package gamedata.item.magic.ring

import domain.Domain
import domain.Domain._
import gamedata.item.magic.MagicItemType
import gamedata.item.magic.ring.Gem.Gem
import gamedata.item.magic.ring.RingPower.RingPower
import gamedata.{Fact, UsesKnowledge}

/** Magic rings */
object RingType extends MagicItemType {
  override type Attribute = Gem

  override implicit def attributeDomain: Domain[Gem] = Gem.domain

  override type Power = RingPower

  override implicit def powerDomain: Domain[RingPower] = RingPower.domain
}

case class Ring(gem: Option[Gem], power: Option[RingPower]) extends RingType.MagicItem {
  override def _merge(that: RingType.MagicItem): Either[String, Ring] = that match {
    case Ring(thatGem, thatPower) => for {
      inferredGem <- gem.merge(thatGem)
      inferredPower <- power.merge(thatPower)
    } yield Ring(inferredGem, inferredPower)
  }

  override def toString: String =
    "a" +
      (attribute match {
        case Some(a) => " " + a.toString
        case None => ""
      }) + " " +
      "ring" +
      (power match {
        case Some(p) => " of " + p.toString
        case None => ""
      })

  override def quantity: Option[Int] = Some(1)

  override def attribute: Option[Gem] = gem

  override def build(attribute: Gem, power: RingPower): Ring = Ring(Some(attribute), Some(power))
}

object Ring {
  def apply(gem: Gem): Ring = new Ring(Some(gem), None)

  implicit def domain: Domain[Ring] = (x: Ring, y: Ring) => x._merge(y)

  implicit def usesKnowledge: UsesKnowledge[Ring] =
    (self: Ring, fact: Fact) => (fact, self.attribute, self.power) match {
      case (RingType.MagicItemKnowledge(_a, _p), Some(a), Some(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (RingType.MagicItemKnowledge(_a, _p: RingPower), Some(a), None) if a == _a => Right(Ring(Some(a), Some(_p)))
      case (RingType.MagicItemKnowledge(_a: Gem, _p), None, Some(p)) if p == _p => Right(Ring(Some(_a), Some(p)))
      case _ => Right(self)
    }
}
