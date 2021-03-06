package gamedata.item.weapon

import _root_.domain.pLift
import domain.Domain._
import gamedata.item.weapon.Missiletype._
import gamedata.item.{Bonus, pItem}

/** A stack of missiles */
case class pMissile(quantity: pLift[Int], missiletype: pLift[MissileType], plusToHit: pLift[Bonus], plusDamage: pLift[Bonus]) extends pWeapon {
  private def _merge(that: pMissile): Either[String, pMissile] = that match {
    case pMissile(thatQuantity, thatMissileType, thatPlusToHit, thatPlusDamage) => for {
      inferredQuantity <- quantity.merge(thatQuantity)
      inferredMissileType <- missiletype.merge(thatMissileType)
      inferredPlusToHit <- plusToHit.merge(thatPlusToHit)
      inferredPlusDamage <- plusDamage.merge(thatPlusDamage)
    } yield pMissile(inferredQuantity, inferredMissileType, inferredPlusToHit, inferredPlusDamage)
  }

  override def merge(that: pItem): Either[String, pItem] = that match {
    case missile: pMissile => _merge(missile)
    case pItem.UNKNOWN => Right(this)
    case _ => Left(s"Incompatible information: $this and $that")
  }

  override def consumeOne: pLift[Option[pItem]] = quantity match {
    case pLift.UNKNOWN => pLift.UNKNOWN
    case pLift.Known(1) => pLift.Known(None)
    case pLift.Known(q) => pLift.Known(Some(pMissile(pLift.Known(q - 1), missiletype, plusToHit, plusDamage)))
  }

  override def toString: String = s"$quantity $plusToHit,$plusDamage ${
    missiletype match {
      case pLift.UNKNOWN => "unknown"
      case pLift.Known(mt) => if (quantity == pLift.Known(1)) mt.singular else mt.plural
    }
  }"
}

object pMissile {
  def apply(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus): pMissile =
    pMissile(pLift.Known(quantity), pLift.Known(missileType), pLift.Known(plusToHit), pLift.Known(plusDamage))

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): pMissile =
    apply(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType): pMissile =
    pMissile(pLift.Known(quantity), pLift.Known(missileType), pLift.UNKNOWN, pLift.UNKNOWN)
}