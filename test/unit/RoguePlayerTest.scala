package unit

import gamedata.Command
import model.{IGameOverObserver, IRoguePlayer, IScreenObserver, RoguePlayer}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import rogue.IRogue

class RoguePlayerTest extends AnyFlatSpec {
  "A RoguePlayer" should "pass on commands to Rogue" in {
    object MockRogue extends IRogue {
      private var _receivedKeypress = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def addScreenObserver(observer: IScreenObserver): Unit = ()

      override def startGame(): Unit = ()
    }

    val player = new RoguePlayer(MockRogue)
    player.performCommand(Command.LEFT)
    MockRogue should be(Symbol("receivedCommand"))
  }
}
