package unit

import gamedata.fact.ProvidesKnowledge._
import gamedata.item.InSlot
import gamedata.item.magic.scroll.Scroll._
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.{Slot, pCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class pCommandTest extends AnyFlatSpec with Matchers {
  "A read scroll command" should "imply all the facts implied by the existence of the scroll" in {
    val command: pCommand = pCommand.Read(Scroll(1, "abcde", ScrollPower.ENCHANT_ARMOR))
    command.implications should contain(ScrollKnowledge("abcde", ScrollPower.ENCHANT_ARMOR))
  }

  "A read scroll command" should "know that the scroll has been consumed" in {
    val command: pCommand = pCommand.Read(Slot.A, Scroll(2, "abcde", ScrollPower.TELEPORTATION))
    command.implications should contain(InSlot(Slot.A, Some(Scroll(1, "abcde", ScrollPower.TELEPORTATION))))
    command.implications should not contain InSlot(Slot.A, Some(Scroll(2, "abcde", ScrollPower.TELEPORTATION)))
  }
}
