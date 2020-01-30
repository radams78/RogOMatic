package gamedata.item.magic.wand

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.item.magic.UnstackableMagicItemType
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandPower.WandPower
import gamedata.item.magic.wand.WandShape.WandShape

/** A wand or staff */
object WandType extends UnstackableMagicItemType {
  override type Attribute = Material

  override implicit def attributeDomain: Domain[Material] = Material.domain

  override type Power = WandPower

  override implicit def powerDomain: Domain[WandPower] = WandPower.domain
}

case class Wand(wandShape: pLift[WandShape], material: pLift[Material], override val power: pLift[WandPower]) extends WandType.UnstackableMagicItem {
  override def toString: String = "a " +
    (material match {
      case pLift.Known(m) => s"$m "
      case pLift.UNKNOWN => ""
    }) + wandShape.toString +
    (power match {
      case pLift.Known(p) => s" of $p"
      case pLift.UNKNOWN => ""
    })

  override def attribute: pLift[Material] = material

  override def _merge(that: WandType.MagicItem): Either[String, WandType.MagicItem] = that match {
    case Wand(thatWandShape, thatMaterial, thatPower) => for {
      inferredWandShape <- wandShape.merge(thatWandShape)
      inferredMaterial <- material.merge(thatMaterial)
      inferredPower <- power.merge(thatPower)
    } yield Wand(inferredWandShape, inferredMaterial, inferredPower)
  }

  override def build(attribute: Material, power: WandPower): WandType.MagicItem = Wand(pLift.UNKNOWN, pLift.Known(attribute), pLift.Known(power))
}

object Wand {
  def apply(wandShape: WandShape, material: Material): Wand = Wand(pLift.Known(wandShape), pLift.Known(material), pLift.UNKNOWN)
}