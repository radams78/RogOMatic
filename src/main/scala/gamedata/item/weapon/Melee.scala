package gamedata.item.weapon

import domain.Domain._
import gamedata.item.weapon.MeleeType.MeleeType
import gamedata.item.weapon.ShooterType.ShooterType
import gamedata.item.{Bonus, pItem}

trait Wieldable extends pWeapon

/** A melee weapon or bow */
case class Melee(wieldableType: MeleeType, plusToHit: Bonus, plusDamage: Bonus) extends Wieldable {
  override def toString: String =
    s"$plusToHit,$plusDamage $wieldableType"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case Melee(thatWieldableType, thatPlusToHit, thatPlusDamage) => for {
      inferredWieldableType <- wieldableType.merge(thatWieldableType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield Melee(inferredWieldableType, inferredPlusToHit, inferredPlusDamage)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}

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