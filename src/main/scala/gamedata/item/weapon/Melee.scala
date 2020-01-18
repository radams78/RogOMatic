package gamedata.item.weapon

import domain.Domain._
import domain.pLift
import gamedata.item.weapon.MeleeType.MeleeType
import gamedata.item.{Bonus, pItem}

/** A melee weapon */
case class Melee(meleeType: pLift[MeleeType], plusToHit: pLift[Bonus], plusDamage: pLift[Bonus]) extends Wieldable {
  private def _merge(that: Melee): Either[String, Melee] = that match {
    case Melee(thatMeleeType, thatPlusToHit, thatPlusDamage) => for {
      inferredMeleeType <- meleeType.merge(thatMeleeType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield Melee(inferredMeleeType, inferredPlusToHit, inferredPlusDamage)
  }

  override def toString: String =
    s"$plusToHit,$plusDamage $meleeType"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case melee: Melee => _merge(melee)
    case pItem.UNKNOWN => Right(this)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}

object Melee {
  def apply(meleeType: MeleeType, plusToHit: Bonus, plusDamage: Bonus): Melee =
    Melee(pLift.Known(meleeType), pLift.Known(plusToHit), pLift.Known(plusDamage))
}