package gamedata.item

import domain.{Domain, pLift}
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.magic.potion.{Colour, Potion}
import gamedata.item.magic.ring.{Gem, Ring}
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.wand.{Material, Wand, WandShape}
import gamedata.item.weapon.Missiletype.MissileType
import gamedata.item.weapon.{WeaponType, pMissile, pWeapon}
import gamedata.{Fact, ProvidesKnowledge}

import scala.util.matching.Regex

/** An item that the PC can pick up 
 *
 * Contract:
 * - implications is monotone
 * - x <= x.infer(fact)
 * - if x.impliciations contains fact then x.infer(fact) == x */
trait pItem {
  def consumeOne: pLift[Option[pItem]]

  def infer(fact: Fact): Either[String, pItem] = Right(this)

  def merge(that: pItem): Either[String, pItem]

  def implications: Set[Fact] = Set()
}

object pItem {

  implicit def providesKnowledge: ProvidesKnowledge[pItem] = (self: pItem) => self.implications

  /** Unknown item
   *
   * Contract: for any item x, 
   * x.merge(UNKNOWN) == x
   * UNKNOWN.merge(x) == x */
  case object UNKNOWN extends pItem {
    override def merge(that: pItem): Either[String, pItem] = Right(that)

    override def implications: Set[Fact] = Set()

    override def consumeOne: pLift[Option[pItem]] = pLift.UNKNOWN
  }

  private val rationsRegex: Regex = """(\d+) rations of food""".r
  private val identifiedArmorRegex: Regex = """([-+]\d+) (\w+(?: \w+)?) \[\d+\]""".r
  private val identifiedWeaponRegex: Regex = """a(?:n?) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val identifiedWeaponsRegex: Regex = """(\d+) ([-+]\d+),([-+]\d+) ([-\w]+(?: \w+)?)""".r
  private val unidentifiedWeaponsRegex: Regex = """(\d+) ([-\w]+(?: \w+)?)""".r
  private val ringRegex: Regex = """a(?:n?) ([-\w]+) ring""".r
  private val potionRegex: Regex = """a(?:n?) ([-\w]+) potion""".r
  private val scrollRegex: Regex = """a scroll entitled: '(\w+(?: \w+)*)'""".r
  private val wandRegex: Regex = """a(?:n?) (\w+) (wand|staff)""".r

  /** Given a description from a displayed inventory, return the corresponding [[pItem]] */
  def parse(description: String): Either[String, pItem] = description match {
    case "some food" => Right(Food(1))
    case rationsRegex(quantity) => Right(Food(quantity.toInt))
    case identifiedArmorRegex(bonus, armorType) => for (at <- ArmorType.parse(armorType)) yield Armor(at, Bonus(bonus.toInt))
    case identifiedWeaponRegex(plusToHit, plusDamage, weaponType) =>
      for (wt <- WeaponType.parse(weaponType)) yield pWeapon(wt, plusToHit.toInt, plusDamage.toInt)
    case unidentifiedWeaponsRegex(quantity, missileType) =>
      for (wt <- WeaponType.parse(missileType)) yield wt match {
        case wt: MissileType => pMissile(quantity.toInt, wt)
        case _ => return Left(s"Expected missile type but received $wt in $description")
      }
    case identifiedWeaponsRegex(quantity, plusToHit, plusDamage, weaponType) =>
      for (wt <- WeaponType.parse(weaponType)) yield wt match {
        case wt: MissileType => weapon.pMissile(quantity.toInt, wt, plusToHit.toInt, plusDamage.toInt)
        case _ => return Left(s"Expected missile type but received $wt in $description")
      }
    case ringRegex(gem) => for (g <- Gem.parse(gem)) yield Ring(g)
    case potionRegex(colour) => for (c <- Colour.parse(colour)) yield Potion(1, c)
    case scrollRegex(title) => Right(Scroll(1, title))
    case wandRegex(material, wandType) => for {
      wt <- WandShape.parse(wandType)
      m <- Material.parse(material)
    } yield Wand(wt, m)
    case description => for (at <- ArmorType.parse(description)) yield Armor(at)
  }

  implicit def domain: Domain[pItem] = (x: pItem, y: pItem) => x.merge(y)
}
