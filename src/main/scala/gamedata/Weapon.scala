package gamedata

import gamedata.Domain._

/** A weapon in the game of Rogue */
trait Weapon extends Item

/** Factory methods for [[Weapon]] */
object Weapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): Weapon = weaponType match {
    case wt: WieldableType => Wieldable(wt, Bonus(plusToHit), Bonus(plusDamage))
    case wt: MissileType => Missile(1, wt, Bonus(plusToHit), Bonus(plusDamage))
  }

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Missile(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType): Missile = Missile(quantity, missileType)
}

/** A melee weapon or bow */
case class Wieldable(wieldableType: WieldableType, plusToHit: Bonus, plusDamage: Bonus) extends Weapon {
  override def toString: String =
    s"$plusToHit,$plusDamage $wieldableType"

  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Wieldable(thatWieldableType, thatPlusToHit, thatPlusDamage) => for {
      inferredWieldableType <- wieldableType.merge(thatWieldableType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield Wieldable(inferredWieldableType, inferredPlusToHit, inferredPlusDamage).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }
}

/** A stack of missiles */
trait Missile extends Weapon

object Missile {
  def apply(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus): Missile =
    Identified(quantity, missileType, plusToHit, plusDamage)

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Identified(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType): Missile = Unidentified(quantity, missileType)

  case class Identified(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus) extends Missile {
    override def toString: String =
      s"$quantity $plusToHit,$plusDamage ${if (quantity > 1) missileType.plural else missileType.singular}"

    override def merge[T <: Item](that: T): Either[String, T] = that match {
      case Identified(thatQuantity, thatMissileType, thatPlusToHit, thatPlusDamage) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
        inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
        inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
      } yield Identified(inferredQuantity, inferredMissileType, inferredPlusToHit, inferredPlusDamage).asInstanceOf[T]
      case Unidentified(thatQuantity, thatMissileType) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Identified(inferredQuantity, inferredMissileType, plusToHit, plusDamage).asInstanceOf[T]
      case _ => Left(s"Incompatible items: $this and $that")
    }
  }

  case class Unidentified(quantity: Int, missileType: MissileType) extends Missile {
    override def merge[T <: Item](that: T): Either[String, T] = that match {
      case Identified(thatQuantity, thatMissileType, thatPlusToHit, thatPlusDamage) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Identified(inferredQuantity, inferredMissileType, thatPlusToHit, thatPlusDamage).asInstanceOf[T]
      case Unidentified(thatQuantity, thatMissileType) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredMissileType <- missileType.merge(thatMissileType)
      } yield Unidentified(inferredQuantity, inferredMissileType).asInstanceOf[T]
      case _ => Left(s"Incompatible items: $this and $that")
    }
  }

}
