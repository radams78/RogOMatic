package mock

import gamedata.Item

case class Weapon(quantity: Int, weaponType: WeaponType, plusToHit: Int, plusDamage: Int) extends Item

object Weapon {
  def apply(weaponType: WeaponType, plusToHit: Int, plusDamage: Int): Weapon =
    Weapon(1, weaponType, plusToHit, plusDamage)
}
