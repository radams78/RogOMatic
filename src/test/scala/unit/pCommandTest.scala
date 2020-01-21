package unit

import gamedata.item.magic.scroll.Scroll._
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.pCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class pCommandTest extends AnyFlatSpec with Matchers {
  "A read scroll command" should "imply all the facts implied by the existence of the scroll" in {
    val command: pCommand = pCommand.Read(Scroll(1, "abcde", ScrollPower.ENCHANT_ARMOR))
    command.implications should contain(ScrollKnowledge("abcde", ScrollPower.ENCHANT_ARMOR))
  }
}
