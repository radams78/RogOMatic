package gamedata.item.weapon

import domain.Domain._
import domain.pLift
import gamedata.item.weapon.ShooterType.ShooterType
import gamedata.item.{Bonus, pItem}

/** A bow */
case class pShooter(shooterType: pLift[ShooterType], plusToHit: pLift[Bonus], plusDamage: pLift[Bonus]) extends Wieldable {
  override def toString: String =
    s"$plusToHit,$plusDamage $shooterType"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case shooter: pShooter => _merge(shooter)
    case pItem.UNKNOWN => Right(this)
    case _ => Left(s"Incompatible item: $this and $that")
  }

  private def _merge(that: pShooter): Either[String, pShooter] = that match {
    case pShooter(thatShooterType, thatPlusToHit, thatPlusDamage) => for {
      inferredWieldableType <- shooterType.merge(thatShooterType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield pShooter(inferredWieldableType, inferredPlusToHit, inferredPlusDamage)
  }

  override def consumeOne: pLift[Option[pItem]] = pLift.Known(None)
}

object pShooter {
  def apply(shooterType: ShooterType, plusToHit: Bonus, plusDamage: Bonus): pShooter =
    pShooter(pLift.Known(shooterType), pLift.Known(plusToHit), pLift.Known(plusDamage))
}