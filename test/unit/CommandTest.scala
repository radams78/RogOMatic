package unit

import model.Command
import model.rogue.{IRogue, Screen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class CommandTest extends AnyFlatSpec {
  "A command" should "send its keypresses to model.rogue.Rogue" in {
    /**
     *
     */
    object MockRogue extends IRogue {
      private var _receivedKeypress: Boolean = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def startGame(): Unit = ()

      override def getScreen: Option[Screen] = None
    }

    Command.LEFT.perform(MockRogue)
    MockRogue should be(Symbol("receivedCommand"))
  }
}
