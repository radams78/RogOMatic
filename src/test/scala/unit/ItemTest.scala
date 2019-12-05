package unit

import gamedata.{Armor, ArmorType, Food, Item}
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
}
