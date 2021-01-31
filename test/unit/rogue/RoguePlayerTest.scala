package unit.rogue

import model.items.Inventory
import model.rogue._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoguePlayerTest extends AnyFlatSpec with Matchers {
  "A Rogue player" should "start the game of Rogue" in {

    var startedGame : Boolean = false
    
    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): Unit = ()

      override def startGame(): Unit = if (startedGame) fail("startGame called twice") else startedGame = true
    }
    
    val player : IRoguePlayer = RoguePlayer(MockRogue)
    player.startGame()
    startedGame shouldBe true
  }
  
  "A Rogue player" should "broadcast the inventory from Rogue" in {
    val screen1contents: String =
      """
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
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin

    val screen1: Screen = Screen.makeScreen(screen1contents)

    val screen2contents: String =
      """                                                --press space to continue--
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
        |
        |
        |
        |
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin

    val screen2: Screen = Screen.makeScreen(screen2contents)

    val screenReader : ScreenReader = ScreenReader()
    object MockRogue extends IRogue {
      private trait MockRogueState {
        def start : MockRogueState = fail("start called twice")
         
        def readScreen: Screen

        def transitions: Map[Char, MockRogueState]

        final def sendKeypress(keypress: Char): MockRogueState =
          transitions.getOrElse(keypress,
            fail("Unexpected keypress '" + keypress + "'")
          )
      }

      private object StateZero extends MockRogueState {
        override def start: MockRogue.MockRogueState = StateOne
        
        override def readScreen: Screen = fail("readScreen called before game started")

        override def transitions: Map[Char, MockRogue.MockRogueState] = Map()
      }
      
      private object StateOne extends MockRogueState {
        override def readScreen: Screen = screen1

        override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo)
      }

      private object StateTwo extends MockRogueState {
        override def readScreen: Screen = screen2

        override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
      }

      private var state: MockRogueState = StateZero

      override def sendKeypress(keypress: Char): Unit = {
        state = state.sendKeypress(keypress)
        screenReader.notify(state.readScreen)
      }

      override def startGame(): Unit = {
        state = state.start
        screenReader.notify(state.readScreen)
      }
    }

    object MockObserver extends IInventoryObserver {
      private var _seenInventory: Boolean = false
      
      def seenInventory: Boolean = _seenInventory
      
      override def notify(inventory: Inventory): Unit = {
        inventory should be(Inventory())
        _seenInventory = true
      }
    }
    
    object MockActuator extends IActuator {
      override def displayInventoryScreen(): Unit = MockRogue.sendKeypress('i')

      override def clearInventoryScreen(): Unit = MockRogue.sendKeypress(' ')

      override def startGame(): Unit = MockRogue.startGame()
    }
    
    val player: IRoguePlayer = RoguePlayer(MockRogue)
    player.addInventoryObserver(MockObserver)
    player.startGame()
    MockObserver should be(Symbol("seenInventory"))
  }
}
