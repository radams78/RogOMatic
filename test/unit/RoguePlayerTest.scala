package unit

import gamedata.Command
import model.{IRoguePlayer, IRoguePlayerObserver, RoguePlayer}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import rogue.IRogue

class RoguePlayerTest extends AnyFlatSpec {
  "A model" should "pass its first screen on to any observers" in {
    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): Unit = 
        fail("Keypress detected")

      override def readScreen: Seq[String] = Seq("The first screen")
    }
    
    object MockObserver extends IRoguePlayerObserver {
      def seenFirstScreen: Boolean = _seenFirstScreen

      private var _seenFirstScreen : Boolean = false

      override def notify(screen: Seq[String]): Unit = 
        if (screen == Seq("The first screen")) 
          _seenFirstScreen = true 
        else fail("Sent unexpected screen: " + screen)
    }
    
    val model : IRoguePlayer = new RoguePlayer(MockRogue)
    model.addObserver(MockObserver)
    model.startGame()
    assert(MockObserver.seenFirstScreen)
  }

  "A RoguePlayer" should "pass on commands to Rogue" in {
    class MockRogue extends IRogue {
      private var _receivedKeypress = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def readScreen: Seq[String] = fail("readScreen called unexpectedly")
    }

    val rogue : MockRogue = new MockRogue()
    val player = new RoguePlayer(rogue)
    player.performCommand(Command.LEFT)
    rogue should be(Symbol("receivedCommand"))
  }
}
