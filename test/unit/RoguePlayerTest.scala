package unit

import model.RoguePlayer
import model.rogue.{IRogue, Screen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoguePlayerTest extends AnyFlatSpec with Matchers {
  "A Rogue player" should "start the game of Rogue" in {
    object MockRogue extends IRogue {
      private var _started: Boolean = false
      
      def started: Boolean = _started
      
      override def sendKeypress(keypress: Char): Unit = ()

      override def startGame(): Unit = _started = true

      override def getScreen: Option[Screen] = None
    }
    
    val player : RoguePlayer = new RoguePlayer(MockRogue)
    player.startGame()
    MockRogue should be(Symbol("started"))
  }
}
