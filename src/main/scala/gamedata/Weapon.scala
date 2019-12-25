package gamedata

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
case class Wieldable(weaponType: WieldableType, plusToHit: Bonus, plusDamage: Bonus) extends Weapon {
  override def toString: String =
    s"$plusToHit,$plusDamage $weaponType"
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
  }

  case class Unidentified(quantity: Int, missileType: MissileType) extends Missile

}
