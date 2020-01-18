package gamedata.item.weapon

import domain.Domain._
import gamedata.item._
import gamedata.item.weapon.Missiletype.MissileType
import gamedata.item.weapon.WieldableType.WieldableType

/** Partial information about a weapon in the game of Rogue */
trait pWeapon extends pItem

/** Factory methods for [[pWeapon]] */
object pWeapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): pWeapon = weaponType match {
    case wt: WieldableType => Wieldable(wt, Bonus(plusToHit), Bonus(plusDamage))
    case wt: MissileType => Missile(1, wt, Bonus(plusToHit), Bonus(plusDamage))
    case WeaponType.SHORT_BOW => SHORT_BOW
  }

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Missile(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType): Missile = Missile(quantity, missileType)
}

/** A melee weapon or bow */
case class Wieldable(wieldableType: WieldableType, plusToHit: Bonus, plusDamage: Bonus) extends pWeapon {
  override def toString: String =
    s"$plusToHit,$plusDamage $wieldableType"

  override def merge(that: pItem): Either[String, pItem] = that match {
    case Wieldable(thatWieldableType, thatPlusToHit, thatPlusDamage) => for {
      inferredWieldableType <- wieldableType.merge(thatWieldableType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield Wieldable(inferredWieldableType, inferredPlusToHit, inferredPlusDamage)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}

/** A stack of missiles */
trait Missile extends pWeapon

object Missile {
  def apply(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus): Missile =
    Identified(quantity, missileType, plusToHit, plusDamage)

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Identified(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType): Missile = Unidentified(quantity, missileType)

  case class Identified(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus) extends Missile {
    override def toString: String =
      s"$quantity $plusToHit,$plusDamage ${if (quantity > 1) missileType.plural else missileType.singular}"

    override def merge(that: pItem): Either[String, pItem] = that match {
      case Identified(thatQuantity, thatMissileType, thatPlusToHit, thatPlusDamage) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
        inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
        inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
      } yield Identified(inferredQuantity, inferredMissileType, inferredPlusToHit, inferredPlusDamage)
      case Unidentified(thatQuantity, thatMissileType) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Identified(inferredQuantity, inferredMissileType, plusToHit, plusDamage)
      case _ => Left(s"Incompatible item: $this and $that")
    }
  }

  case class Unidentified(quantity: Int, missileType: MissileType) extends Missile {
    override def merge(that: pItem): Either[String, pItem] = that match {
      case Identified(thatQuantity, thatMissileType, thatPlusToHit, thatPlusDamage) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Identified(inferredQuantity, inferredMissileType, thatPlusToHit, thatPlusDamage)
      case Unidentified(thatQuantity, thatMissileType) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Unidentified(inferredQuantity, inferredMissileType)
      case _ => Left(s"Incompatible item: $this and $that")
    }
  }

}

case object SHORT_BOW extends pWeapon {
  override def merge(that: pItem): Either[String, pItem] = that match {
    case SHORT_BOW => Right(SHORT_BOW)
    case pItem.UNKNOWN => Right(SHORT_BOW)
    case _ => Left(s"Incompatible information: $this and $that")
  }
}