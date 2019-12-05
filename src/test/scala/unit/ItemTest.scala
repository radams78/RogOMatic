package unit

import gamedata._
import org.scalatest.flatspec.AnyFlatSpec

class ItemTest extends AnyFlatSpec {
  "Some food" should "be recognised as some food" in {
    assertResult(Food(1)) {
      Item.parse("some food")
    }
  }

  "2 rations of food" should "be recognised as 2 rations of food" in {
    assertResult(Food(2)) {
      Item.parse("2 rations of food")
    }
  }

  "a suit of ring mail" should "be recognised as a suit of ring mail" in {
    assertResult(Armor(ArmorType.RING_MAIL, +1)) {
      Item.parse("+1 ring mail [4]")
    }
  }

  "a long sword" should "be recognised as a long sword" in {
    assertResult(Weapon(WeaponType.LONG_SWORD, +1, 0)) {
      Item.parse("a +1,+0 long sword")
    }
  }

  "31 arrows" should "be recognised as 31 arrows" in {
    assertResult(Weapon(31, WeaponType.ARROW, +0, +0)) {
      Item.parse("31 +0,+0 arrows")
    }
  }
}
