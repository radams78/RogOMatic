package unit

import integration.{IRogue, IView, RogOMatic}
import org.scalatest.funsuite.AnyFunSuite

class RogOMaticTest extends AnyFunSuite {
  test("RogOMatic should pass the first screen to the view") {
    object MockRogue extends IRogue {
      override def readScreen: Seq[String] = Seq("The first screen")

      override def sendKeypress(keypress: Char): Unit = fail("Keypress received")
    }
    object MockView extends IView {
      var seenFirstScreen: Boolean = false

      override def notify(screen: Seq[String]): Unit = {
        assert(screen == Seq("The first screen"))
        seenFirstScreen = true
      }
    }
    
    val rogomatic : RogOMatic = new RogOMatic(MockRogue, MockView)
    rogomatic.startGame()
    assert(MockView.seenFirstScreen)
  }
}
