package unit

import gamedata._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ItemTest extends AnyFlatSpec with Matchers {
  "Some food" should "be recognised as some food" in {
    Item.parse("some food") should contain(Food(1))
  }

  "2 rations of food" should "be recognised as 2 rations of food" in {
    Item.parse("2 rations of food") should contain(Food(2))
  }

  "a suit of ring mail" should "be recognised as a suit of ring mail" in {
    Item.parse("+1 ring mail [4]") should contain(Armor(ArmorType.RING_MAIL, +1))
  }

  "a long sword" should "be recognised as a long sword" in {
    Item.parse("a +1,+0 long sword") should contain(Weapon(WeaponType.LONG_SWORD, +1, +0))
  }

  "31 arrows" should "be recognised as 31 arrows" in {
    Item.parse("31 +0,+0 arrows") should contain(Weapon(31, WeaponType.ARROW, +0, +0))
  }
}
