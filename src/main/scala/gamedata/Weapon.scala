package gamedata

/** A weapon in the game of Rogue */
trait Weapon extends Item

/** Factory methods for [[Weapon]] */
object Weapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): Weapon = weaponType match {
    case wt: WieldableType => Wieldable(wt, Bonus(plusToHit), Bonus(plusDamage))
    case wt: MissileType => Missile(1, wt, Bonus(plusToHit), Bonus(plusDamage))
  }

  def apply(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    IdentifiedMissile(quantity, weaponType, Bonus(plusToHit), Bonus(plusDamage))

  def apply(quantity: Int, weaponType: WeaponType): Missile = UnidentifiedMissile(quantity, weaponType)
}

/** A melee weapon or bow */
case class Wieldable(weaponType: WieldableType, plusToHit: Bonus, plusDamage: Bonus) extends Weapon {
  override def toString: String =
    s"$plusToHit,$plusDamage $weaponType"
}

/** A stack of missiles */
trait Missile extends Weapon

case class IdentifiedMissile(quantity: Int, weaponType: MissileType, plusToHit: Bonus, plusDamage: Bonus) extends Missile {
  override def toString: String =
    s"$quantity $plusToHit,$plusDamage ${if (quantity > 1) weaponType.plural else weaponType.singular}"
}

object Missile {
  def apply(quantity: Int, weaponType: MissileType, plusToHit: Bonus, plusDamage: Bonus): Missile =
    IdentifiedMissile(quantity, weaponType, plusToHit, plusDamage)

  def apply(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Missile(quantity, weaponType, Bonus(plusToHit), Bonus(plusDamage))
}

case class UnidentifiedMissile(quantity: Int, weaponType: WeaponType) extends Missile
