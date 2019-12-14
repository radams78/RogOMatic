package gamedata

import scala.util.matching.Regex

/** An item that the PC can pick up */
class Item

object Item {
  private val rationRegex: Regex = """(\d+) rations of food""".r
  private val identifiedArmorRegex: Regex = """([-+]\d+) (\w+(?: \w+)?) \[\d+\]""".r
  private val weaponRegex: Regex = """a(?:n?) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val weaponsRegex: Regex = """(\d+) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val ringRegex: Regex = """a(?:n?) ([-\w]+) ring""".r
  private val potionRegex: Regex = """a(?:n?) ([\w]+) potion""".r
  private val scrollRegex: Regex = """a scroll entitled: '(\w+(?: \w+)*)'""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Either[String, Item] = description match {
    case "some food" => Right(Food(1))
    case rationRegex(quantity) => Right(Food(quantity.toInt))
    case identifiedArmorRegex(bonus, armorType) => for (at <- ArmorType.parse(armorType)) yield Armor(at, Bonus(bonus.toInt))
    case weaponRegex(plusToHit, plusDamage, weaponType) =>
      for (wt <- WeaponType.parse(weaponType)) yield Weapon(wt, plusToHit.toInt, plusDamage.toInt)
    case weaponsRegex(quantity, plusToHit, plusDamage, weaponType) => WeaponType.parse(weaponType) match {
      case Right(wt: MissileType) => Right(Missile(quantity.toInt, wt, plusToHit.toInt, plusDamage.toInt))
      case Right(wt) => Left(s"Expected missile type but received $wt in $description")
      case Left(err) => Left(err)
    }
    case ringRegex(gem) => for (g <- Gem.parse(gem)) yield Ring(g)
    case potionRegex(colour) => for (c <- Colour.parse(colour)) yield Potion(1, c)
    case scrollRegex(title) => Right(Scroll(1, title))
    case description => ArmorType.parse(description) match {
      case Left(_) => Left(s"Unrecognised item: $description")
      case Right(at) => Right(Armor(at))
    }
  }
}
