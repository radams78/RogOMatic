package gamedata.item.weapon

import domain.Domain._
import gamedata.item.weapon.ShooterType.ShooterType
import gamedata.item.{Bonus, pItem}

case class Shooter(shooterType: ShooterType, plusToHit: Bonus, plusDamage: Bonus) extends Wieldable {
  override def toString: String =
    s"$plusToHit,$plusDamage $shooterType"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case Shooter(thatShooterType, thatPlusToHit, thatPlusDamage) => for {
      inferredWieldableType <- shooterType.merge(thatShooterType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield Shooter(inferredWieldableType, inferredPlusToHit, inferredPlusDamage)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}
