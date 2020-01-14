package unit

import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.magic.potion.{Colour, Potion}
import gamedata.item.magic.ring.{Gem, Ring}
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.wand.{Material, Wand, WandShape}
import gamedata.item.weapon.{Weapon, WeaponType, WieldableType}
import gamedata.item.{weapon, _}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ItemTest extends AnyFlatSpec with Matchers with EitherValues {
  "Some food" should "be recognised as some food" in {
    Item.parse("some food") should be(Right(Food(1)))
  }

  "2 rations of food" should "be recognised as 2 rations of food" in {
    Item.parse("2 rations of food") should be(Right(Food(2)))
  }

  "a suit of ring mail" should "be recognised as a suit of ring mail" in {
    Item.parse("+1 ring mail [4]") should be(Right(Armor(ArmorType.RING_MAIL, +1)))
  }

  "a long sword" should "be recognised as a long sword" in {
    Item.parse("a +1,+0 long sword") should be(Right(Weapon(WieldableType.LONG_SWORD, +1, +0)))
  }

  "identified arrows" should "be recognised as identified arrows" in {
    Item.parse("31 +0,+0 arrows") should be(Right(weapon.Weapon(31, WeaponType.ARROW, +0, +0)))
  }

  "unidentified arrows" should "be recognised as unidentified arrows" in {
    Item.parse("31 arrows") should be(Right(weapon.Weapon(31, WeaponType.ARROW)))
  }

  "a stibotantalite ring" should "be recognised as a stibotantalite ring" in {
    Item.parse("a stibotantalite ring") should be(Right(Ring(Gem.STIBOTANTALITE)))
  }

  "a yellow potion" should "be recognised as a yellow potion" in {
    Item.parse("a yellow potion") should be(Right(Potion(1, Colour.YELLOW)))
  }

  "a scroll" should "be recognised as a scroll" in {
    Item.parse("a scroll entitled: 'coph rech ack'") should be(Right(Scroll(1, "coph rech ack")))
  }

  "a redwood staff" should "be recognised as a redwood staff" in {
    Item.parse("a redwood staff") should be(Right(Wand(WandShape.STAFF, Material.REDWOOD)))
  }


}
