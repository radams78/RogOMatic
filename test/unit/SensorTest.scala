package unit

import model.{IGameOverObserver, IScoreObserver, Sensor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Screen

class SensorTest extends AnyFlatSpec with Matchers {
  "A sensor" should "report the final score" in {
    val screenContents: String =
      """quit with 20 gold-more-
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
        |                                                      ----------------+--
        |                                                      |.................|
        |                                                      |.................|
        |                                                      |.................|
        |                                                      |..@..............|
        |                                                      +.................|
        |                                                      -------------------
        |Level: 1  Gold: 20      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0""".stripMargin

    val screen = Screen.makeScreen(screenContents)

    object MockObserver extends IScoreObserver {
      private var _seenScore = false

      def seenScore: Boolean = _seenScore

      override def notifyScore(score: Int): Unit = {
        score should be(20)
        _seenScore = true
      }
    }

    val sensor: Sensor = new Sensor
    sensor.addScoreObserver(MockObserver)
    sensor.notify(screen)
    MockObserver should be(Symbol("seenScore"))
  }

  "A sensor" should "recognize a game over screen" in {
    val screenContents =
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

    val screen = Screen.makeScreen(screenContents)

    object MockObserver extends IGameOverObserver {
      private var _seenGameOverScreen: Boolean = false

      def seenGameOverScreen: Boolean = _seenGameOverScreen

      override def notifyGameOver(): Unit = {
        _seenGameOverScreen = true
      }
    }

    val sensor: Sensor = new Sensor
    sensor.addGameOverObserver(MockObserver)
    sensor.notify(screen)
    MockObserver should be(Symbol("seenGameOverScreen"))
  }
}
