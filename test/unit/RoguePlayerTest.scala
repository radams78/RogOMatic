package unit

import model.{IScreenObserver, RoguePlayer}
import model.rogue.{IRogue, Screen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoguePlayerTest extends AnyFlatSpec with Matchers {
  def fixture: Object {
    val screen: Screen

    val rogue: IRogue
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
    
    object MockRogue extends IRogue {
      private var _started: Boolean = false

      def started: Boolean = _started

      override def sendKeypress(keypress: Char): Unit = ()

      override def startGame(): Unit = _started = true

      override def getScreen: Option[Screen] = if (_started) Some(screen) else None
    }
  }

  "A Rogue player" should "start the game of Rogue" in {
    val f: Object {
      val screen: Screen

      val rogue: IRogue
    } = fixture
    
    
    val player : RoguePlayer = new RoguePlayer(f.rogue)
    player.startGame()
    f.rogue should be(Symbol("started"))
  }
  
  "A Rogue player" should "broadcast the screen from Rogue" in {
    val f: Object {
      val screen: Screen

      val rogue: IRogue
    } = fixture
    
    object MockObserver extends IScreenObserver {
      private var _seenScreen: Boolean = false
      
      def seenScreen: Boolean = _seenScreen
      
      override def notify(_screen: Screen): Unit = {
        _screen should be(f.screen)
        _seenScreen = true
      }
    }
    
    val player : RoguePlayer = new RoguePlayer(f.rogue)
    player.addScreenObserver(MockObserver)
    player.startGame()
    MockObserver should be(Symbol("seenScreen"))
  }
}
