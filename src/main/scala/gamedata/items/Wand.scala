package gamedata.items

import domain.Domain._
import gamedata.items.Material.Material
import gamedata.items.WandType.WandType

/** A wand or staff */
case class Wand(wandType: WandType, material: Material) extends Item {
  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Wand(thatWandType, thatMaterial) => for {
      inferredWandType <- wandType.merge(thatWandType)
      inferredMaterial <- material.merge(thatMaterial)
    } yield Wand(inferredWandType, inferredMaterial).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }

  override def toString: String = s"a $material $wandType"
}
