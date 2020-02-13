package rogue

import gamedata.MonsterType.MonsterType
import gamedata.item.armor.ArmorType.ArmorType
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.magic.potion.Colour.Colour
import gamedata.item.magic.potion.{Colour, Potion}
import gamedata.item.magic.ring.Gem.Gem
import gamedata.item.magic.ring.{Gem, Ring}
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.wand.Material.Material
import gamedata.item.magic.wand.WandShape.WandShape
import gamedata.item.magic.wand.{Material, Wand, WandShape}
import gamedata.item.weapon.Missiletype.MissileType
import gamedata.item.weapon.{Missiletype, WeaponType, pWeapon}
import gamedata.item.{Bonus, Food}
import gamedata.{Event, MonsterType, ParsableEnum, Slot}

import scala.language.postfixOps
import scala.util.parsing.combinator.RegexParsers

object RogueParsers extends RegexParsers {
  private val article: Parser[Unit] = ("an" | "a") ^^^ ()
  private val posInt: Parser[Int] =
    """\d+""".r ^^ {
      _.toInt
    }
  private val bonus: Parser[Bonus] = """[-+]\d+""".r ^^ { bonus: String => Bonus(bonus.toInt) }
  val slot: Parser[Slot] = accept("slot", { case c: Char => Slot(c) })

  def enum(e: ParsableEnum): Parser[e.Value] =
    e.values.foldLeft[Parser[e.Value]](RogueParsers.failure(s"Unrecognised ${e.name}"))(
      { case (parser, value) => parser | value.toString ^^^ value }
    )

  private val armorType: Parser[ArmorType] = enum(ArmorType)
  private val missileType: Parser[MissileType] =
    ("darts" | "dart") ^^^ Missiletype.DART |
      ("arrows" | "arrow") ^^^ Missiletype.ARROW |
      ("daggers" | "dagger") ^^^ Missiletype.DAGGER |
      ("shurikens" | "shuriken") ^^^ Missiletype.SHURIKEN
  private val weaponType: Parser[WeaponType] = missileType ^^ {
    _.asInstanceOf[WeaponType]
  } | // TODO 
    "short bow" ^^^ WeaponType.SHORT_BOW |
    "mace" ^^^ WeaponType.MACE |
    "long sword" ^^^ WeaponType.LONG_SWORD |
    "two-handed sword" ^^^ WeaponType.TWO_HANDED_SWORD
  private val gem: Parser[Gem] = enum(Gem)
  private val colour: Parser[Colour] = enum(Colour)
  private val material: Parser[Material] = enum(Material)
  private val wandShape: Parser[WandShape] = enum(WandShape)
  private val monsterType: Parser[MonsterType] = enum(MonsterType)

  val pItem: Parser[gamedata.item.pItem] =
    "some food" ^^^ Food(1) |
      posInt <~ "rations of food" ^^ {
        Food
      } |
      bonus ~ armorType ~ "[" ~ posInt ~ "]" ^^ { case bonus ~ armorType ~ _ ~ _ ~ _ => Armor(armorType, bonus) } |
      article ~ bonus ~ "," ~ bonus ~ weaponType ^^ { case _ ~ plusToHit ~ _ ~ plusDamage ~ weaponType => pWeapon(weaponType, plusToHit, plusDamage) } |
      posInt ~ bonus ~ "," ~ bonus ~ missileType ^^ { case quantity ~ plusToHit ~ _ ~ plusDamage ~ missileType =>
        pWeapon(quantity, missileType, plusToHit, plusDamage)
      } |
      posInt ~ missileType ^^ { case quantity ~ missileType => pWeapon(quantity, missileType) } |
      article ~ gem ~ "ring" ^^ { case _ ~ gem ~ _ => Ring(gem) } |
      article ~ colour ~ "potion" ^^ { case _ ~ colour ~ _ => Potion(1, colour) } |
      """a scroll entitled: '""" ~ """\w+( \w+)*""".r ~ """'""" ^^ { case _ ~ title ~ _ => Scroll(1, title) } |
      article ~ material ~ wandShape ^^ { case _ ~ material ~ wandShape => Wand(wandShape, material) } |
      armorType ^^ {
        Armor(_)
      }

  /*  def parser: RogueParsers.Parser[pItem] = oneFood | rations | identifiedArmor | identifiedWeapon | unidentifiedWeapons |
      identifiedWeapons | ring | potion | scroll | wand | armor */

  val event: Parser[Event] =
    posInt ~ "pieces of gold" ^^ { case quantity ~ _ => Event.Gold(quantity) } |
      "you begin to feel better" ^^^ Event.HEALING |
      "you feel as though someone is watching over you" ^^^ Event.REMOVE_CURSE |
      "the" ~ monsterType ~ "misses" ^^ { case _ ~ monsterType ~ _ => Event.MissedBy(monsterType) } |
      "you hit" ^^^ Event.PC_HIT |
      "the" ~ monsterType ~ "hit" ^^ { case _ ~ monsterType ~ _ => Event.HitBy(monsterType) } |
      pItem ~ "(" ~ slot ~ ")" ^^ { case item ~ _ ~ slot ~ _ => Event.PickedUp(slot, item) }

  val events: Parser[Seq[Event]] = event *

  def useParser[T](parser: Parser[T], input: String): Either[String, T] = parseAll(parser, input) match {
    case Success(t, _) => Right(t)
    case Failure(msg, _) => Left(msg)
    case Error(err, _) => Left(err)
  }
}
