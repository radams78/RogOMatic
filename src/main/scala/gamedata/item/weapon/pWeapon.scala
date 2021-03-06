package gamedata.item.weapon

import gamedata.item._
import gamedata.item.weapon.MeleeType.MeleeType
import gamedata.item.weapon.Missiletype.MissileType
import gamedata.item.weapon.ShooterType.ShooterType

/** Partial information about a weapon in the game of Rogue */
trait pWeapon extends pItem

/** Factory methods for [[pWeapon]] */
object pWeapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): pWeapon = weaponType match {
    case wt: MeleeType => Melee(wt, Bonus(plusToHit), Bonus(plusDamage))
    case wt: MissileType => pMissile(1, wt, Bonus(plusToHit), Bonus(plusDamage))
    case wt: ShooterType => pShooter(wt, Bonus(plusToHit), Bonus(plusDamage))
  }

  def apply(quantity: Int, missileType: MissileType, plusToHit: Int, plusDamage: Int): pMissile =
    pMissile(quantity, missileType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, missileType: MissileType, plusToHit: Bonus, plusDamage: Bonus): pMissile =
    pMissile(quantity, missileType, plusToHit, plusDamage) // TODO Duplication

  def apply(weaponType: WeaponType, plusToHit: Bonus, plusDamage: Bonus): pWeapon = weaponType match {
    case wt: MeleeType => Melee(wt, plusToHit, plusDamage)
    case wt: MissileType => pMissile(1, wt, plusToHit, plusDamage)
    case wt: ShooterType => pShooter(wt, plusToHit, plusDamage)
  } // TODO Duplication

  def apply(quantity: Int, missileType: MissileType): pMissile = pMissile(quantity, missileType)
}
