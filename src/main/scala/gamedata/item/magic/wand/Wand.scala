package gamedata.item.magic.wand

import domain.Domain._
import gamedata.item.Item
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandType.WandType

/** A wand or staff */
case class Wand(wandType: WandType, material: Material) extends Item {
  override def merge(that: Item): Either[String, Item] = that match {
    case Wand(thatWandType, thatMaterial) => for {
      inferredWandType <- wandType.merge(thatWandType)
      inferredMaterial <- material.merge(thatMaterial)
    } yield Wand(inferredWandType, inferredMaterial)
    case _ => Left(s"Incompatible item: $this and $that")
  }

  override def toString: String = s"a $material $wandType"
}
