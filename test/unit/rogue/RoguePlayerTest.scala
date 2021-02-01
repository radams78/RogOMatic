package unit.rogue

import model.items.Inventory
import model.rogue._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoguePlayerTest extends AnyFlatSpec with Matchers {
  "A Rogue player" should "start the game of Rogue" in {
    var startedGame : Boolean = false
    
    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): String = ""

      override def startGame(): String = 
        if (startedGame) fail("startGame called twice") else {
          startedGame = true
          ""
        }
    }
    
    val player : IRoguePlayer = RoguePlayer(MockRogue)
    player.startGame()
    startedGame shouldBe true
  }

  "A Rogue player" should "broadcast the first screen" in {
    val screenContents: String = "The screen"
    val screen: Screen = Screen.makeScreen(screenContents)
    
    var seenScreen : Boolean = false

    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): String = ""

      override def startGame(): String = screenContents
    }

    object MockObserver extends IScreenObserver {
      override def notify(_screen: Screen): Unit = {
        _screen shouldBe screen
        seenScreen = true
      }
    }
    
    val player : IRoguePlayer = RoguePlayer(MockRogue)
    player.addScreenObserver(MockObserver)
    player.startGame()
    seenScreen shouldBe true
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

    object MockRogue extends IRogue {
      private trait MockRogueState {
        def start : MockRogueState = fail("start called twice")
         
        def readScreen: String

        def transitions: Map[Char, MockRogueState]

        final def sendKeypress(keypress: Char): MockRogueState =
          transitions.getOrElse(keypress,
            fail("Unexpected keypress '" + keypress + "'")
          )
      }

      private object StateZero extends MockRogueState {
        override def start: MockRogue.MockRogueState = StateOne
        
        override def readScreen: String = fail("readScreen called before game started")

        override def transitions: Map[Char, MockRogue.MockRogueState] = Map()
      }
      
      private object StateOne extends MockRogueState {
        override def readScreen: String = screen1contents

        override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo)
      }

      private object StateTwo extends MockRogueState {
        override def readScreen: String = screen2contents

        override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
      }

      private var state: MockRogueState = StateZero

      override def sendKeypress(keypress: Char): String = {
        state = state.sendKeypress(keypress)
        state.readScreen
      }

      override def startGame(): String = {
        state = state.start
        state.readScreen
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
    
    val player: IRoguePlayer = RoguePlayer(MockRogue)
    player.addInventoryObserver(MockObserver)
    player.startGame()
    MockObserver should be(Symbol("seenInventory"))
  }
}
