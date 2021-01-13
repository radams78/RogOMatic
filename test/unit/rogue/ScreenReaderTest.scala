package unit.rogue

import model.rogue.{IActuator, IRogue, IScreenObserver, RoguePlayer, Screen, ScreenReader}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScreenReaderTest extends AnyFlatSpec with Matchers {
  def fixture: Object {
    val screen: Screen

    val rogue: IRogue

    val screenReader: ScreenReader
  } = new {
    val screen: Screen = Screen.makeScreen(
      """
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
        |  -------+---
        |  |.@.......|
        |  |.........|
        |  |.........|
        |  |........*|
        |  -----------
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
    )

    val rogue : IRogue = MockRogue

    val screenReader: ScreenReader = ScreenReader()

    object MockRogue extends IRogue {
      private var _started: Boolean = false

      def started: Boolean = _started

      override def sendKeypress(keypress: Char): Unit = ()

      override def startGame(): Unit = {
        _started = true
        screenReader.notify(screen)
      }
    }
  }

  "A Rogue player" should "broadcast the screen from Rogue" in {
    val f: Object {
      val screen: Screen

      val rogue: IRogue

      val screenReader: ScreenReader
    } = fixture

    object MockObserver extends IScreenObserver {
      private var _seenScreen: Boolean = false

      def seenScreen: Boolean = _seenScreen

      override def notify(_screen: Screen): Unit = {
        _screen should be(f.screen)
        _seenScreen = true
      }
    }

    object MockActuator extends IActuator {
      override def displayInventoryScreen(): Unit = f.rogue.sendKeypress('i')

      override def clearInventoryScreen(): Unit = f.rogue.sendKeypress(' ')

      override def startGame(): Unit = f.rogue.startGame()
    }

    f.screenReader.addScreenObserver(MockObserver)
    f.screenReader.notify(f.screen)
    MockObserver should be(Symbol("seenScreen"))
  }

}
