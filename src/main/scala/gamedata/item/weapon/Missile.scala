package gamedata.item.weapon

import domain.Domain._
import gamedata.item.weapon.Missiletype._
import gamedata.item.{Bonus, pItem}

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