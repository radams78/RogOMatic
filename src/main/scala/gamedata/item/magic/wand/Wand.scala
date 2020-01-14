package gamedata.item.magic.wand

import domain.Domain
import domain.Domain._
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandPower.WandPower
import gamedata.item.magic.wand.WandShape.WandType
import gamedata.item.{Item, MagicItemType}

/** A wand or staff */
object WandType extends MagicItemType {
  override type Attribute = Material

  override implicit def attributeDomain: Domain[Material] = Material.domain

  override type Power = WandPower

  override implicit def powerDomain: Domain[WandPower] = WandPower.domain
}

case class Wand(wandShape: WandType, material: Material) extends Item {
  override def merge(that: Item): Either[String, Item] = that match {
    case Wand(thatWandType, thatMaterial) => for {
      inferredWandType <- wandShape.merge(thatWandType)
      inferredMaterial <- material.merge(thatMaterial)
    } yield Wand(inferredWandType, inferredMaterial)
    case _ => Left(s"Incompatible item: $this and $that")
  }

  override def toString: String = s"a $material $wandShape"
}
