package unit

import model.{IGameOverObserver, Sensor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import rogue.{IRogue, IScreenObserver}

class SensorTest extends AnyFlatSpec with Matchers {
  "A sensor" should "recognize a game over screen" in {
    val sensor = new Sensor

    val screen4 =
      """quit with 10 gold-more-
        |
        |
        |
        |
        |
        |
        |
        |                               ----------+---------
        |                               |.........@....%...|
        |                               +........?.........|
        |                               |...............*..|
        |                               |..!...............|
        |                               |..................+
        |                               -------+------------
        |
        |
        |
        |
        |
        |
        |
        |
        |Level: 1  Gold: 10      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |"""

    val screen4lines = screen4.stripMargin.split("\n").map(_.padTo(80, ' '))

    val screen5 =
      """-more-
        |
        |
        |                              Top  Ten  Rogueists
        |
        |
        |
        |
        |Rank   Score   Name
        |
        | 1      1224   robin: died of starvation on level 11
        |
        |
        |
        |
        |
        |
        |
        |
        |
        |
        |
        |
        |
        |"""

    val screen5lines = screen5.stripMargin.split("\n").map(_.padTo(80, ' '))

    object MockObserver extends IGameOverObserver {
      private var _seenGameOverScreen = false

      def seenGameOverScreen: Boolean = _seenGameOverScreen

      /** Notify the observer that the game is over
       *
       * @param score The final score */
      override def notifyGameOver(score: Int): Unit = {
        score should be(10)
        _seenGameOverScreen = true
      }
    }

    sensor.addGameOverObserver(MockObserver)
    sensor.notify(screen4lines)
    sensor.notify(screen5lines)
    MockObserver should be(Symbol("seenGameOverScreen"))
  }

}
