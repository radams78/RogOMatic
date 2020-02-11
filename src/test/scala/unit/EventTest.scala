package unit

import gamedata.{Event, MonsterType}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EventTest extends AnyFunSuite with Matchers {
  test("Recognise a hit by message") {
    Event.interpretMessage("the bat hit") should be(Right(Seq(Event.HitBy(MonsterType.BAT))))
  }

  test("Parse a message line with two messages") {
    Event.interpretMessage("you hit  the hobgoblin hit") should be(Right(Seq(Event.PC_HIT, Event.HitBy(MonsterType.HOBGOBLIN))))
  }
}
