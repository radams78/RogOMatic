package gamedata.item.magic.wand

import domain.Domain
import domain.Domain._
import gamedata.item.MagicItemType
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandPower.WandPower
import gamedata.item.magic.wand.WandShape.WandShape

/** A wand or staff */
object WandType extends MagicItemType {
  override type Attribute = Material

  override implicit def attributeDomain: Domain[Material] = Material.domain

  override type Power = WandPower

  override implicit def powerDomain: Domain[WandPower] = WandPower.domain
}

case class Wand(wandShape: Option[WandShape], material: Option[Material], power: Option[WandPower]) extends WandType.MagicItem {
  override def quantity: Option[Int] = Some(1)

  override def attribute: Option[Material] = material

  override def merge(that: WandType.MagicItem): Either[String, WandType.MagicItem] = that match {
    case Wand(thatWandShape, thatMaterial, thatPower) => for {
      inferredWandType <- wandShape.merge(thatWandShape)
      inferredMaterial <- material.merge(thatMaterial)
      inferredPower <- power.merge(thatPower)
    } yield Wand(inferredWandType, inferredMaterial, inferredPower)
  }
}

object Wand {
  def apply(wandShape: WandShape, material: Material): Wand = Wand(Some(wandShape), Some(material), None)
}