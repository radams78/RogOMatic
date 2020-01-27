package unit

import gamedata.item.Food
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.{Report, Slot, pInventory}
import gamestate.History
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, Event}

class HistoryTest extends AnyFlatSpec with Matchers {
  "Reading the new inventory" should "update the inventory correctly" in {
    (for {
      report1 <- Report.GameOn.build("", pInventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(2, "abcde")), None, None), Set())
      report2 <- Report.GameOn.build("", pInventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(1, ScrollPower.REMOVE_CURSE)), None, None), Set(Event.REMOVE_CURSE))
      history <- History.FirstMove(report1).nextMove(Command.Read(Slot.B), report2)
    } yield history) match {
      case Right(h: History.GameOn) => h.inventory should be(pInventory(Map(Slot.A -> Food(1), Slot.B -> Scroll(1, "abcde", ScrollPower.REMOVE_CURSE)), None, None))
      case Right(h) => fail(s"Unexpected history: $h")
      case Left(err) => fail(err)
    }
  }
}
