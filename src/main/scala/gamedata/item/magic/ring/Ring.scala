package gamedata.item.magic.ring

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.fact.{Fact, UsesKnowledge}
import gamedata.item.magic.UnstackableMagicItemType
import gamedata.item.magic.ring.Gem.Gem
import gamedata.item.magic.ring.RingPower.RingPower

/** Magic rings */
object RingType extends UnstackableMagicItemType {
  override type Attribute = Gem

  override implicit def attributeDomain: Domain[Gem] = Gem.domain

  override type Power = RingPower

  override implicit def powerDomain: Domain[RingPower] = RingPower.domain
}

case class Ring(gem: pLift[Gem], power: pLift[RingPower]) extends RingType.UnstackableMagicItem {
  override def _merge(that: RingType.MagicItem): Either[String, Ring] = that match {
    case Ring(thatGem, thatPower) => for {
      inferredGem <- gem.merge(thatGem)
      inferredPower <- power.merge(thatPower)
    } yield Ring(inferredGem, inferredPower)
  }

  override def toString: String =
    "a" +
      (attribute match {
        case pLift.Known(a) => " " + a.toString
        case pLift.UNKNOWN => ""
      }) + " " +
      "ring" +
      (power match {
        case pLift.Known(p) => " of " + p.toString
        case pLift.UNKNOWN => ""
      })

  override def attribute: pLift[Gem] = gem

  override def build(attribute: Gem, power: RingPower): Ring = Ring(pLift.Known(attribute), pLift.Known(power))
}

object Ring {
  def apply(gem: Gem): Ring = new Ring(pLift.Known(gem), pLift.UNKNOWN)

  implicit def domain: Domain[Ring] = (x: Ring, y: Ring) => x._merge(y)

  implicit def usesKnowledge: UsesKnowledge[Ring] =
    (self: Ring, fact: Fact) => (fact, self.attribute, self.power) match {
      case (RingType.MagicItemKnowledge(_a, _p), pLift.Known(a), pLift.Known(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (RingType.MagicItemKnowledge(_a, _p: RingPower), pLift.Known(a), pLift.UNKNOWN) if a == _a => Right(Ring(pLift.Known(a), pLift.Known(_p)))
      case (RingType.MagicItemKnowledge(_a: Gem, _p), pLift.UNKNOWN, pLift.Known(p)) if p == _p => Right(Ring(pLift.Known(_a), pLift.Known(p)))
      case _ => Right(self)
    }
}
