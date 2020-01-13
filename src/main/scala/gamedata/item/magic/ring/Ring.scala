package gamedata.item.magic.ring

import domain.Domain
import domain.Domain._
import gamedata.item.magic.ring.Gem.Gem
import gamedata.item.magic.ring.RingPower.RingPower
import gamedata.item.{Item, UnstackableMagicItemType}

/** A ring */
object RingType extends UnstackableMagicItemType {
  override def name: String = "ring"

  override type Attribute = Gem

  override implicit def attributeDomain: Domain[Gem] = Gem.domain

  override type Power = RingPower

  override implicit def powerDomain: Domain[RingPower] = RingPower.domain
}

case class Ring(gem: Gem) extends Item {
  override def merge(that: Item): Either[String, Item] = that match {
    case Ring(thatGem) => for {inferredGem <- gem.merge(thatGem)} yield Ring(inferredGem)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}
