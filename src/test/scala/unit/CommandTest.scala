package unit

import gamedata.item.magic.scroll.Scroll._
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Command

class CommandTest extends AnyFlatSpec with Matchers {
  "A read scroll command" should "imply all the facts implied by the existence of the scroll" in {
    val command: Command = Command.Read(Scroll(1, "abcde", ScrollPower.ENCHANT_ARMOR))
    command.implications should contain(ScrollKnowledge("abcde", ScrollPower.ENCHANT_ARMOR))
  }
}
