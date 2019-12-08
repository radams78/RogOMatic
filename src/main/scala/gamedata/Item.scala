package gamedata

import scala.util.matching.Regex

/** An item that the PC can pick up */
class Item

object Item {
  private val rationRegex: Regex = """(\d+) rations of food""".r
  private val armorRegex: Regex = """([-+]\d+) (\w+(?: \w+)?) \[\d+\]""".r
  private val weaponRegex: Regex = """a(?:n?) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val weaponsRegex: Regex = """(\d+) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val ringRegex: Regex = """a(?:n?) ([-\w]+) ring""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Either[String, Item] = description match {
    case "some food" => Right(Food(1))
    case rationRegex(quantity) => Right(Food(quantity.toInt))
    case armorRegex(bonus, armorType) => ArmorType.parse(armorType) match {
      case Some(at) => Right(Armor(at, Bonus(bonus.toInt)))
      case None => Left(s"Unrecognised armor type: $armorType in $description")
    }
    case weaponRegex(plusToHit, plusDamage, weaponType) =>
      Right(Weapon(WeaponType.parse(weaponType), plusToHit.toInt, plusDamage.toInt))
    case weaponsRegex(quantity, plusToHit, plusDamage, weaponType) => WeaponType.parse(weaponType) match {
      case wt: MissileType => Right(Missile(quantity.toInt, wt, plusToHit.toInt, plusDamage.toInt))
      case _ => Left(s"Unrecognised missile type: $weaponType in $description")
    }
    case ringRegex(gem) => for (g <- Gem.parse(gem)) yield Ring(g)
    case _ => Left(s"Unrecognised item: $description") // TODO Use Either for error handling everywhere
  }
}
