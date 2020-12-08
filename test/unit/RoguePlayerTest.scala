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

      override def notify(screen: Seq[String]): Unit = {
        screen should be(Seq("The first screen"))
        _seenFirstScreen = true
      }
    }
    
    val model : IRoguePlayer = new RoguePlayer(MockRogue)
    model.addObserver(MockObserver)
    model.startGame()
    MockObserver should be(Symbol("seenFirstScreen"))
  }

  "A RoguePlayer" should "pass on commands to Rogue" in {
    object MockRogue extends IRogue {
      private var _receivedKeypress = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def readScreen: Seq[String] = fail("readScreen called unexpectedly")
    }

    val player = new RoguePlayer(MockRogue)
    player.performCommand(Command.LEFT)
    MockRogue should be(Symbol("receivedCommand"))
  }

  "A RoguePlayer" should "read the screen after performing a command" in {
    object MockRogue extends IRogue {
      private var _receivedKeypress = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      override def readScreen: Seq[String] = {
        _receivedKeypress should be(true)
        Seq("The screen")
      }
    }

    object MockObserver extends IRoguePlayerObserver {
      private var _seenScreen = false

      def seenScreen: Boolean = _seenScreen

      /** Notify all observers that this is the screen displayed by Rogue */
      override def notify(screen: Seq[String]): Unit = {
        screen should be(Seq("The screen"))
        _seenScreen = true
      }
    }

    val player = new RoguePlayer(MockRogue)
    player.addObserver(MockObserver)
    player.performCommand(Command.LEFT)
    MockObserver should be(Symbol("seenScreen"))
  }

}
