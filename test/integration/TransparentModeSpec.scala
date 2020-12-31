package integration

import model.items._
import model.rogue.{IRogue, IScreenObserver, RoguePlayer, Screen}
import model.{Command, IGameOverObserver, IInventoryObserver, IScoreObserver}
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

      val screen2: Screen = Screen.makeScreen(screen2contents)

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

      val screen3: Screen = Screen.makeScreen(screen3contents)

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

      val screen4: Screen = Screen.makeScreen(screen4contents)

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

      val screen5: Screen = Screen.makeScreen(screen5contents)

      object MockRogue extends IRogue {
        def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer

        private trait MockRogueState {
          def readScreen: Option[Screen]

          def transitions: Map[Char, MockRogueState]

          def receivedQuitCommand: Boolean = false

          final def sendKeypress(keypress: Char): MockRogueState =
            transitions.getOrElse(keypress,
              fail("Unexpected keypress '" + keypress + "'")
            )
        }

        private object StateOne extends MockRogueState {
          override def readScreen: Option[Screen] = Some(screen1)

          override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo, 'Q' -> StateThree)
        }

        private object StateTwo extends MockRogueState {
          override def readScreen: Option[Screen] = Some(screen2)

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
        }

        private object StateThree extends MockRogueState {
          override def readScreen: Option[Screen] = Some(screen3)

          override def transitions: Map[Char, MockRogueState] = Map('y' -> StateFour)
        }

        private object StateFour extends MockRogueState {
          override def readScreen: Option[Screen] = Some(screen4)

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateFive)
        }

        private object StateFive extends MockRogueState {
          override def readScreen: Option[Screen] = Some(screen5)

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateSix)
        }

        private object StateSix extends MockRogueState {
          override def readScreen: Option[Screen] = None

          override def transitions: Map[Char, MockRogueState] = Map()

          override def receivedQuitCommand: Boolean = true
        }

        private var state: MockRogueState = StateOne

        def receivedQuitCommand: Boolean = state.receivedQuitCommand

        override def sendKeypress(keypress: Char): Unit = {
          state = state.sendKeypress(keypress)
          for (observer <- screenObservers) {
            for (screen <- state.readScreen)
              observer.notify(screen)
          }
        }

        var screenObservers: Set[IScreenObserver] = Set()

        override def startGame(): Unit = for (observer <- screenObservers)
          for (screen <- state.readScreen)
            observer.notify(screen)
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
      val player: RoguePlayer = new RoguePlayer(MockRogue)
      player.addScreenObserver(MockScreenView)
      player.addInventoryObserver(MockInventoryView)
      player.addGameOverObserver(MockGameOverView)
      player.addScoreObserver(MockScoreView)
      player.startGame()

      Then("the user should see the first screen")
      MockScreenView should be(Symbol("seenFirstScreen"))

      And("the user should see the first inventory")
      MockInventoryView should be(Symbol("seenFirstInventory"))
      
      When("the user enters the command to quit")
      player.performCommand(Command.QUIT)

      Then("Rogue should receive the command to quit")
      MockRogue should be(Symbol("receivedQuitCommand"))

      And("the user should see the final score")
      MockScoreView.getScore should be(0)

      And("the user should see the game over message")
      MockGameOverView should be(Symbol("seenGameOverScreen"))
    }

  }
}