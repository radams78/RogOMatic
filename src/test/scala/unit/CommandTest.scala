package unit

import gamedata.Fact
import gamedata.ProvidesKnowledge._
import gamedata.items.{Scroll, ScrollPower}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Command

class CommandTest extends AnyFlatSpec with Matchers {
  "A read scroll command" should "imply all the facts implied by the existence of the scroll" in {
    val command: Command = Command.Read(Scroll("abcde", ScrollPower.ENCHANT_ARMOR))
    command.implications should contain(Fact.ScrollKnowledge("abcde", ScrollPower.ENCHANT_ARMOR))
  }
}