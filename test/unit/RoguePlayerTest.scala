package unit

import integration.{IRoguePlayer, IModelObserver, RoguePlayer}
import org.scalatest.flatspec.AnyFlatSpec
import rogue.IRogue

class RoguePlayerTest extends AnyFlatSpec {
  "A model" should "pass its first screen on to any observers" in {
    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): Unit = 
        fail("Keypress detected")

      override def readScreen: Seq[String] = Seq("The first screen")
    }
    
    object MockObserver extends IModelObserver {
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
}
