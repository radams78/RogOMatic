package unit

import gamedata.Slot
import org.scalatest.flatspec.AnyFlatSpec

class SlotTest extends AnyFlatSpec {
  "two slots" should "be ordered alphabetically" in {
    assert(Slot.B < Slot.E)
  }
}
