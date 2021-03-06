package integration

import gamedata.Command
import model.{IGameOverObserver, IScoreObserver, Sensor}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue.{IRogue, IScreenObserver, Screen}

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

      object MockUser extends IScreenObserver with IGameOverObserver with IScoreObserver {
        private var _seenGameOverScreen: Boolean = false
        private var _seenFirstScreen: Boolean = false
        private var _score: Option[Int] = None

        def getScore: Int = _score.getOrElse(fail("getScore called before score seen"))
        def seenGameOverScreen: Boolean = _seenGameOverScreen
        def seenFirstScreen: Boolean = _seenFirstScreen

        /** Notify all observers that this is the screen displayed by Rogue */
        override def notify(screen: Screen): Unit = if (screen == screen1) _seenFirstScreen = true

        /** Notify the observer that the game is over */
        override def notifyGameOver(): Unit = _seenGameOverScreen = true

        override def notifyScore(score: Int): Unit = _score = Some(score)
      }

      Given("a new game of Rogue")
      val sensor: Sensor = new Sensor
      MockRogue.addScreenObserver(sensor)
      MockRogue.addScreenObserver(MockUser)
      sensor.addGameOverObserver(MockUser)
      sensor.addScoreObserver(MockUser)
      MockRogue.startGame()

      Then("the user should see the first screen")
      MockUser should be(Symbol("seenFirstScreen"))

      When("the user enters the command to quit")
      Command.QUIT.perform(MockRogue)

      Then("Rogue should receive the command to quit")
      MockRogue should be(Symbol("receivedQuitCommand"))

      And("the user should see the final score")
      MockUser.getScore should be(0)

      And("the user should see the game over message")
      MockUser should be(Symbol("seenGameOverScreen"))
    }

  }
}