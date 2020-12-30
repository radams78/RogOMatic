package unit

import model.Command
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import model.rogue.{IRogue, IScreenObserver}

class CommandTest extends AnyFlatSpec {
  "A command" should "send its keypresses to Rogue" in {
    object MockRogue extends IRogue {
      private var _receivedKeypress: Boolean = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def addScreenObserver(observer: IScreenObserver): Unit = ()

      override def startGame(): Unit = ()
    }

    Command.LEFT.perform(MockRogue)
    MockRogue should be(Symbol("receivedCommand"))
  }
}
