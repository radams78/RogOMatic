package unit

import gamedata.fact.ProvidesKnowledge._
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.item.{Food, InSlot}
import gamedata.{Report, Slot}
import gamestate.{History, Inventory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, Event}

class HistoryTest extends AnyFlatSpec with Matchers {
  "Reading the new inventory" should "update the inventory correctly" in {
    (for {
      report1 <- Report.GameOn.build("", Inventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(2, "abcde")), None, None), Set())
      report2 <- Report.GameOn.build("", Inventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(1, ScrollPower.REMOVE_CURSE)), None, None), Set(Event.REMOVE_CURSE))
      history <- History.FirstMove(report1).nextMove(Command.Read(Slot.B), report2)
    } yield history) match {
      case Right(h: History.GameOn) => h.inventory should be(Inventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(1, "abcde", ScrollPower.REMOVE_CURSE)), None, None))
      case Right(h) => fail(s"Unexpected history: $h")
      case Left(err) => fail(err)
    }
  }

  "Reading the new inventory" should "update the _implications correctly" in {
    (for {
      report1 <- Report.GameOn.build("", Inventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(2, "abcde")), None, None), Set())
      report2 <- Report.GameOn.build("", Inventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(1, ScrollPower.REMOVE_CURSE)), None, None), Set(Event.REMOVE_CURSE))
      history <- History.FirstMove(report1).nextMove(Command.Read(Slot.B), report2)
    } yield history) match {
      case Right(h: History.GameOn) =>
        h.implications should not contain InSlot(Slot.B, Some(Scroll(2, "abcde", ScrollPower.REMOVE_CURSE)))
      case Right(h) => fail(s"Unexpected history: $h")
      case Left(err) => fail(err)
    }
  }
}
