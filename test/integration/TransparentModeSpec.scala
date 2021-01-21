package integration

import model.items._
import model.rogue._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

class TransparentModeSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  Feature("Transparent Mode") {
    Scenario("User quits immediately") {
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
        """                                                a) some food
          |                                                b) +1 ring mail [4] being worn
          |                                                c) a +1,+1 mace in hand
          |                                                d) a +1,+0 short bow
          |                                                e) 32 +0,+0 arrows
          |                                                --press space to continue--
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

      val inventory1 : Inventory = Inventory(
        Map(
          Slot.A -> Food,
          Slot.B -> RingMail(+1),
          Slot.C -> Mace(+1, +1),
          Slot.D -> ShortBow(+1, +0),
          Slot.E -> Arrows(32, +0, +0)
        ),
        wearing = Slot.B,
        wielding = Slot.C
      )
      
      val screen3contents: String =
        """really quit?
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

      val screen4contents: String =
        """quit with 0 gold-more-
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

      val screen5contents: String =
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
          |""".stripMargin

      object MockRogue {
        object MockStarter extends Starter {
          override def start(): Unit = state = state.start()

          override def sendBytes(bytes: Array[Byte]): Unit = state = state.sendBytes(bytes)
        }
        
        object MockBuffer extends Buffer {
          override def getScreenLines: String = state.getScreenLines
        }

        private trait MockRogueState {
          def sendBytes(bytes: Array[Byte]): MockRogueState  = {
            if (bytes.length != 1) fail("sendBytes() called with " + bytes.mkString(""))
            val keypress: Char = bytes.head.toChar
            transitions.getOrElse(keypress,fail("Unexpected keypress '" + keypress + "'"))
          }

          def start(): MockRogueState = fail("start() called twice")

          def getScreenLines: String
          
          def transitions: Map[Char, MockRogueState]

          def receivedQuitCommand: Boolean = false
        }

        private object StateZero extends MockRogueState {
          override def start(): MockRogue.MockRogueState = StateOne

          override def getScreenLines: String = ""

          override def transitions: Map[Char, MockRogue.MockRogueState] = Map()
        }
        
        private object StateOne extends MockRogueState {
          override def getScreenLines: String = screen1contents

          override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo, 'Q' -> StateThree)
        }

        private object StateTwo extends MockRogueState {
          override def getScreenLines: String = screen2contents

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
        }

        private object StateThree extends MockRogueState {
          override def getScreenLines: String = screen3contents

          override def transitions: Map[Char, MockRogueState] = Map('y' -> StateFour)
        }

        private object StateFour extends MockRogueState {
          override def getScreenLines: String = screen4contents

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateFive)
        }

        private object StateFive extends MockRogueState {
          override def getScreenLines: String = screen5contents

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateSix)
        }

        private object StateSix extends MockRogueState {
          override def getScreenLines: String = ""

          override def transitions: Map[Char, MockRogueState] = Map()

          override def receivedQuitCommand: Boolean = true
        }

        private var state: MockRogueState = StateZero

        def receivedQuitCommand: Boolean = state.receivedQuitCommand
      }

      object MockScreenView extends IScreenObserver {
        private var _seenFirstScreen : Boolean = false
        def seenFirstScreen: Boolean = _seenFirstScreen
        override def notify(screen: Screen): Unit = if (screen == screen1) _seenFirstScreen = true
      }

      object MockInventoryView extends IInventoryObserver {
        private var _seenFirstInventory : Boolean = false
        def seenFirstInventory : Boolean = _seenFirstInventory
        override def notify(inventory : Inventory) : Unit = if (inventory == inventory1) _seenFirstInventory = true
      }
      
      object MockScoreView extends IScoreObserver {
        private var _score : Option[Int] = None
        def getScore : Int = _score.getOrElse(fail("getScore called before score seen"))
        override def notifyScore(score: Int): Unit = _score = Some(score)
      }
      
      object MockGameOverView extends IGameOverObserver {
        private var _seenGameOverScreen : Boolean = false
        def seenGameOverScreen : Boolean = _seenGameOverScreen
        override def notifyGameOver(): Unit = _seenGameOverScreen = true
      }
      
      Given("a new game of Rogue")
      val screenReader: ScreenReader = ScreenReader()
      val game : RogueGame = RogueGame(MockRogue.MockStarter, MockRogue.MockBuffer, screenReader)
      game.startGame()

      Then("the user should see the first screen")
      MockScreenView should be(Symbol("seenFirstScreen"))

      And("the user should see the first inventory")
      MockInventoryView should be(Symbol("seenFirstInventory"))
      
      When("the user enters the command to quit")
      game.performCommand(Command.QUIT)

      Then("model.rogue.Rogue should receive the command to quit")
      MockRogue should be(Symbol("receivedQuitCommand"))

      And("the user should see the final score")
      MockScoreView.getScore should be(0)

      And("the user should see the game over message")
      MockGameOverView should be(Symbol("seenGameOverScreen"))
    }

  }
}