package unit

import main.RogOMatic
import org.scalatest.funsuite.AnyFunSuite
import rogue.IRogue
import view.IView

class RogOMaticTest extends AnyFunSuite {
  test("RogOMatic should pass the first screen to the view") {
    val firstScreen: Seq[String] = Seq("The first screen")

    object MockRogue extends IRogue {
      override def readScreen: Seq[String] = firstScreen

      override def sendKeypress(keypress: Char): Unit = fail("Keypress received")
    }
    
    object MockView extends IView {
      var seenFirstScreen: Boolean = false

      override def notify(screen: Seq[String]): Unit = {
        assert(screen == firstScreen)
        seenFirstScreen = true
      }
    }
    
    val rogomatic : RogOMatic = new RogOMatic(MockRogue, MockView)
    rogomatic.startGame()
    assert(MockView.seenFirstScreen)
  }
}
