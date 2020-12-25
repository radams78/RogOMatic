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
      val screen1: String =
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
          |"""
          
      val screen1lines: Screen = Screen.makeScreen(screen1)

      val screen2: String =
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
          |"""

      val screen2lines = Screen.makeScreen(screen2)

      val screen3 =
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
          |"""

      val screen3lines = Screen.makeScreen(screen3)

      val screen4 =
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
          |"""

      val screen5lines = Screen.makeScreen(screen5)

      object MockRogue extends IRogue {
        def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)


        private trait MockRogueState {
          def readScreen: Screen

          def transitions: Map[Char, MockRogueState]

          def receivedQuitCommand: Boolean = false

          final def sendKeypress(keypress: Char): MockRogueState =
            transitions.getOrElse(keypress,
              fail("Unexpected keypress '" + keypress + "'")
            )
        }

        private object StateOne extends MockRogueState {
          override def readScreen: Screen = screen1lines

          override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo, 'Q' -> StateThree)
        }

        private object StateTwo extends MockRogueState {
          override def readScreen: Screen = screen2lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
        }

        private object StateThree extends MockRogueState {
          override def readScreen: Screen = screen3lines

          override def transitions: Map[Char, MockRogueState] = Map('y' -> StateFour)
        }

        private object StateFour extends MockRogueState {
          override def readScreen: Screen = screen4lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateFive)
        }

        private object StateFive extends MockRogueState {
          override def readScreen: Screen = screen5lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateSix)
        }

        private object StateSix extends MockRogueState {
          override def readScreen: Screen = fail("Attempted to read screen after Rogue process ended")

          override def transitions: Map[Char, MockRogueState] = Map()

          override def receivedQuitCommand: Boolean = true
        }

        private var state: MockRogueState = StateOne

        def receivedQuitCommand: Boolean = state.receivedQuitCommand

        override def sendKeypress(keypress: Char): Unit = state = state.sendKeypress(keypress)

        var _screenObserver: Option[IScreenObserver] = None

        override def startGame(): Unit = for (observer <- _screenObserver) observer.notify(state.readScreen)
      }

      object MockUser extends IScreenObserver with IGameOverObserver with IScoreObserver {
        private var _seenGameOverScreen: Boolean = false
        private var _seenFirstScreen: Boolean = false
        private var _score: Option[Int] = None

        def getScore: Int = _score.getOrElse(fail("getScore called before score seen"))
        def seenGameOverScreen: Boolean = _seenGameOverScreen
        def seenFirstScreen: Boolean = _seenFirstScreen

        /** Notify all observers that this is the screen displayed by Rogue */
        override def notify(screen: Screen): Unit = if (screen == screen1lines) _seenFirstScreen = true

        /** Notify the observer that the game is over */
        override def notifyGameOver(): Unit = _seenGameOverScreen = true

        override def notify(score: Int): Unit = _score = Some(score)
      }

      Given("a new game of Rogue")
      val sensor = new Sensor
      MockRogue.addScreenObserver(sensor)
      MockRogue.addScreenObserver(MockUser)
      sensor.addGameOverObserver(MockUser)
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