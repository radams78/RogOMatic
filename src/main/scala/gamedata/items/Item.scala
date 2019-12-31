package gamedata.items

import domain.Domain

import scala.util.matching.Regex

/** An item that the PC can pick up */
trait Item {
  def merge[T <: Item](that: T): Either[String, T]
}

object Item {
  private val rationsRegex: Regex = """(\d+) rations of food""".r
  private val identifiedArmorRegex: Regex = """([-+]\d+) (\w+(?: \w+)?) \[\d+\]""".r
  private val identifiedWeaponRegex: Regex = """a(?:n?) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val identifiedWeaponsRegex: Regex = """(\d+) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val unidentifiedWeaponsRegex: Regex = """(\d+) ([-\w]+(?: \w+)?)""".r
  private val ringRegex: Regex = """a(?:n?) ([-\w]+) ring""".r
  private val potionRegex: Regex = """a(?:n?) ([-\w]+) potion""".r
  private val scrollRegex: Regex = """a scroll entitled: '(\w+(?: \w+)*)'""".r
  private val wandRegex: Regex = """a(?:n?) (\w+) (wand|staff)""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Either[String, Item] = description match {
    case "some food" => Right(Food(1))
    case rationsRegex(quantity) => Right(Food(quantity.toInt))
    case identifiedArmorRegex(bonus, armorType) => for (at <- ArmorType.parse(armorType)) yield Armor(at, Bonus(bonus.toInt))
    case identifiedWeaponRegex(plusToHit, plusDamage, weaponType) =>
      for (wt <- WeaponType.parse(weaponType)) yield Weapon(wt, plusToHit.toInt, plusDamage.toInt)
    case unidentifiedWeaponsRegex(quantity, missileType) =>
      for (wt <- WeaponType.parse(missileType)) yield wt match {
        case wt: MissileType => Missile(quantity.toInt, wt)
        case _ => return Left(s"Expected missile type but received $wt in $description")
      }
    case identifiedWeaponsRegex(quantity, plusToHit, plusDamage, weaponType) =>
      for (wt <- WeaponType.parse(weaponType)) yield wt match {
        case wt: MissileType => Missile(quantity.toInt, wt, plusToHit.toInt, plusDamage.toInt)
        case _ => return Left(s"Expected missile type but received $wt in $description")
      }
    case ringRegex(gem) => for (g <- Gem.parse(gem)) yield Ring(g)
    case potionRegex(colour) => for (c <- Colour.parse(colour)) yield Potion(1, c)
    case scrollRegex(title) => Right(Scroll(1, title))
    case wandRegex(material, wandType) => for {
      wt <- WandType.parse(wandType)
      m <- Material.parse(material)
    } yield Wand(wt, m)
    case description => for (at <- ArmorType.parse(description)) yield Armor(at)
  }

  implicit def domain: Domain[Item] = (x: Item, y: Item) => x.merge(y)
}
