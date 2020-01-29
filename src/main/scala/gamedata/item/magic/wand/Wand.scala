package gamedata.item.magic.wand

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.item.magic.MagicItemType
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandPower.WandPower
import gamedata.item.magic.wand.WandShape.WandShape
import gamedata.item.pItem

/** A wand or staff */
object WandType extends MagicItemType {
  override type Attribute = Material

  override implicit def attributeDomain: Domain[Material] = Material.domain

  override type Power = WandPower

  override implicit def powerDomain: Domain[WandPower] = WandPower.domain
}

case class Wand(wandShape: pLift[WandShape], material: pLift[Material], power: pLift[WandPower]) extends WandType.MagicItem {
  override def toString: String = "a " +
    (material match {
      case pLift.Known(m) => s"$m "
      case pLift.UNKNOWN => ""
    }) + wandShape.toString +
    (power match {
      case pLift.Known(p) => s" of $p"
      case pLift.UNKNOWN => ""
    })

  override def quantity: pLift[Int] = pLift.Known(1)

  override def attribute: pLift[Material] = material

  override def _merge(that: WandType.MagicItem): Either[String, WandType.MagicItem] = that match {
    case Wand(thatWandShape, thatMaterial, thatPower) => for {
      inferredWandType <- wandShape.merge(thatWandShape)
      inferredMaterial <- material.merge(thatMaterial)
      inferredPower <- power.merge(thatPower)
    } yield Wand(inferredWandType, inferredMaterial, inferredPower)
  }

  override def build(attribute: Material, power: WandPower): Wand = Wand(pLift.UNKNOWN, pLift.Known(attribute), pLift.Known(power))

  override def consumeOne: pLift[Option[pItem]] = pLift.Known(None) // TODO Duplication?
}

object Wand {
  def apply(wandShape: WandShape, material: Material): Wand = Wand(pLift.Known(wandShape), pLift.Known(material), pLift.UNKNOWN)
}