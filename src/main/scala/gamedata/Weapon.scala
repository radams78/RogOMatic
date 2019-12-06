package gamedata

/** A weapon in the game of Rogue */
trait Weapon extends Item

/** A melee weapon or bow */
case class Wieldable(weaponType: WieldableType, plusToHit: Int, plusDamage: Int) extends Weapon {
  override def toString: String =
    s"${if (plusToHit >= 0) "+" else ""}$plusToHit,${if (plusDamage >= 0) "+" else ""}$plusDamage $weaponType"
}

/** A stack of missiles */
case class Missile(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int) extends Weapon {
  override def toString: String =
    s"$quantity ${if (plusToHit >= 0) "+" else ""}$plusToHit,${if (plusDamage >= 0) "+" else ""}$plusDamage " + // TODO Extract method to print bonus
      s"${if (quantity > 0) weaponType.singular else weaponType.plural}"
}

/** Factory methods for [[Weapon]] */
object Weapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): Weapon = weaponType match {
    case wt: WieldableType => Wieldable(wt, plusToHit, plusDamage)
    case wt: MissileType => Missile(1, wt, plusToHit, plusDamage)
  }

  def apply(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Missile(quantity, weaponType, plusToHit, plusDamage)
}
