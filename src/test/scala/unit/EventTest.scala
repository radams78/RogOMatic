package unit

import gamedata.item.magic.potion.{Colour, Potion}
import gamedata.{Event, MonsterType, Slot}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EventTest extends AnyFunSuite with Matchers {
  test("Recognise a hit by message") {
    Event.interpretMessage("the bat hit") should be(Right(Seq(Event.HitBy(MonsterType.BAT))))
  }

  test("Parse a message line with two messages") {
    Event.interpretMessage("you hit  the hobgoblin hit") should be(Right(Seq(Event.PC_HIT, Event.HitBy(MonsterType.HOBGOBLIN))))
  }

  test("Parse a message about picking up an object") {
    Event.interpretMessage("a purple potion (f)") should be(Right(Seq(Event.PickedUp(Slot.F, Potion(1, Colour.PURPLE)))))
  }
}
