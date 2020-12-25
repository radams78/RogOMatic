package unit

import model.{IGameOverObserver, Sensor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Screen

class SensorTest extends AnyFlatSpec with Matchers {
  "A sensor" should "recognize a game over screen" in {
    val sensor: Sensor = new Sensor

    val screen4 =
      """quit with 20 gold-more-
        |                                                      --------------------
        |                                                      +..%......@..*S....|
        |                                                      |............./....|
        |                                                      -------+------------
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
        |
        |
        |
        |
        |
        |Level: 1  Gold: 20      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |"""

    val screen4lines = Screen.makeScreen(screen4)

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
        | 1         0   robin: quit on level 1
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

    val screen5lines = Screen.makeScreen(screen5)

    object MockObserver extends IGameOverObserver {
      private var _seenGameOverScreen: Boolean = false

      def seenGameOverScreen: Boolean = _seenGameOverScreen

      override def notifyGameOver(): Unit = {
        _seenGameOverScreen = true
      }
    }

    sensor.addGameOverObserver(MockObserver)
    sensor.notify(screen4lines)
    sensor.notify(screen5lines)
    MockObserver should be(Symbol("seenGameOverScreen"))
  }
}
