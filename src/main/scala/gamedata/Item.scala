package gamedata

import scala.util.matching.Regex

/** An item that the PC can pick up */
class Item

object Item {
  private val rationRegex: Regex = """(\d+) rations of food""".r
  private val armorRegex: Regex = """([-+]\d+) (\w+(?: \w+)?) \[\d+\]""".r
  private val weaponRegex: Regex = """a(?:n?) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val weaponsRegex: Regex = """(\d+) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Option[Item] = description match {
    case "some food" => Some(Food(1))
    case rationRegex(quantity) => Some(Food(quantity.toInt))
    case armorRegex(bonus, armorType) => for (at <- ArmorType.parse(armorType)) yield Armor(at, Bonus(bonus.toInt))
    case weaponRegex(plusToHit, plusDamage, weaponType) =>
      Some(Weapon(WeaponType.parse(weaponType), plusToHit.toInt, plusDamage.toInt))
    case weaponsRegex(quantity, plusToHit, plusDamage, weaponType) => WeaponType.parse(weaponType) match {
      case wt: MissileType => Some(Missile(quantity.toInt, wt, Bonus(plusToHit.toInt), Bonus(plusDamage.toInt))) // TODO Make factory methods consistent
      case _ => None // TODO Better error handling
    }
    case _ => None
  }
}
