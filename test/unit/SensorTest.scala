package unit

import model.{IGameOverObserver, Sensor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Screen

class SensorTest extends AnyFlatSpec with Matchers {
  "A sensor" should "recognize a game over screen" in {
    val sensor: Sensor = new Sensor

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
    sensor.notify(screen5lines)
    MockObserver should be(Symbol("seenGameOverScreen"))
  }
}
