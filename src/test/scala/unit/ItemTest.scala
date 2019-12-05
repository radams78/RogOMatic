package unit

import gamedata.{Food, Item}
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
}
