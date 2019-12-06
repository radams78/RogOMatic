package gamedata

trait Weapon extends Item

case class Wieldable(weaponType: WieldableType, plusToHit: Int, plusDamage: Int) extends Weapon {
  override def toString: String =
    s"${if (plusToHit > 0) "+" else ""}$plusToHit,${if (plusDamage > 0) "+" else ""}$plusDamage $weaponType"
}

case class Missile(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int) extends Weapon {
  override def toString: String =
    s"$quantity ${if (plusToHit > 0) "+" else ""}$plusToHit,${if (plusDamage > 0) "+" else ""}$plusDamage " +
      s"${if (quantity > 0) weaponType.singular else weaponType.plural}"
}

object Weapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): Weapon = weaponType match {
    case wt: WieldableType => Wieldable(wt, plusToHit, plusDamage)
    case wt: MissileType => Missile(1, wt, plusToHit, plusDamage)
  }

  def apply(quantity: Int, weaponType: MissileType, plusToHit: Int, plusDamage: Int): Missile =
    Missile(quantity, weaponType, plusToHit, plusDamage)
}
