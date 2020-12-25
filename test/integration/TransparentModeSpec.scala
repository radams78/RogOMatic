package integration

import gamedata.Command
import model.{IGameOverObserver, Sensor}
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

      object MockUser extends IScreenObserver with IGameOverObserver {

        private trait MockUserState {
          def seenGameOverScreen: Boolean = false

          def seenFirstScreen: Boolean = false

          def notify(screen: Screen): MockUserState

          def notifyGameOver: MockUserState
        }

        private object StateOne extends MockUserState {
          override def notify(screen: Screen): MockUserState = {
            assert(screen === screen1lines || screen === screen2lines)
            StateTwo
          }


          override def notifyGameOver: MockUserState =
            fail("Received game over message unexpectedly")
        }

        private object StateTwo extends MockUserState {
          override def notify(screen: Screen): MockUserState = {
            assert(screen === screen1lines || screen === screen2lines || screen === screen3lines || screen === screen4lines || screen === screen5lines)
            this
          }

          override def notifyGameOver: MockUserState = StateThree

          override def seenFirstScreen: Boolean = true
        }

        private object StateThree extends MockUserState {
          override def notify(screen: Screen): MockUserState =
            fail("Received screen after game should be over")

          override def notifyGameOver: MockUserState =
            fail("Received game over message twice")

          override def seenFirstScreen: Boolean = true

          override def seenGameOverScreen: Boolean = true
        }

        private var state: MockUserState = StateOne: MockUserState

        override def notify(screen: Screen): Unit = state = state.notify(screen)

        def seenFirstScreen: Boolean = state.seenFirstScreen

        def seenGameOverScreen: Boolean = state.seenGameOverScreen

        /** Notify the observer that the game is over */
        override def notifyGameOver(): Unit = state = state.notifyGameOver
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

      And("the user should see the game over message")
      MockUser should be(Symbol("seenGameOverScreen"))
    }

  }
}