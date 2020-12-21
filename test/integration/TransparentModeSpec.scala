package integration

import gamedata.Command
import model.{IGameOverObserver, Sensor}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue.{IRogue, IScreenObserver}

class TransparentModeSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  Feature("Transparent Mode") {
    Scenario("User quits immediately") {
      Given("a new game of Rogue")
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
          
      val screen1lines: Array[String] =
        screen1.stripMargin.split("\n").map(_.padTo(80, ' '))

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

      val screen2lines = screen2.stripMargin.split("\n").map(_.padTo(80, ' '))

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

      val screen3lines = screen3.stripMargin.split("\n").map(_.padTo(80, ' '))

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

      val screen4lines = screen4.stripMargin.split("\n").map(_.padTo(80, ' '))

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

      val screen5lines = screen5.stripMargin.split("\n").map(_.padTo(80, ' '))

      object MockRogue extends IRogue {
        def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)


        private trait MockRogueState {
          def readScreen: Seq[String]

          def transitions: Map[Char, MockRogueState]

          def receivedQuitCommand: Boolean = false

          final def sendKeypress(keypress: Char): MockRogueState =
            transitions.getOrElse(keypress,
              fail("Unexpected keypress '" + keypress + "'")
            )
        }

        private object StateOne extends MockRogueState {
          override def readScreen: Seq[String] = screen1lines

          override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo, 'Q' -> StateThree)
        }

        private object StateTwo extends MockRogueState {
          override def readScreen: Seq[String] = screen2lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
        }

        private object StateThree extends MockRogueState {
          override def readScreen: Seq[String] = screen3lines

          override def transitions: Map[Char, MockRogueState] = Map('y' -> StateFour)
        }

        private object StateFour extends MockRogueState {
          override def readScreen: Seq[String] = screen4lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateFive)
        }

        private object StateFive extends MockRogueState {
          override def readScreen: Seq[String] = screen5lines

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateSix)
        }

        private object StateSix extends MockRogueState {
          override def readScreen: Seq[String] = fail("Attempted to read screen after Rogue process ended")

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

          def notify(screen: Seq[String]): MockUserState

          def notifyGameOver(score: Int): MockUserState
        }

        private object StateOne extends MockUserState {
          override def notify(screen: Seq[String]): MockUserState = {
            assert(screen === screen1lines || screen === screen2lines)
            StateTwo
          }


          override def notifyGameOver(score: Int): MockUserState =
            fail("Received game over message unexpectedly")
        }

        private object StateTwo extends MockUserState {
          override def notify(screen: Seq[String]): MockUserState = {
            assert(screen === screen1lines || screen === screen2lines || screen === screen3lines || screen === screen4lines || screen === screen5lines)
            this
          }

          override def notifyGameOver(score: Int): MockUserState = StateThree

          override def seenFirstScreen: Boolean = true
        }

        private object StateThree extends MockUserState {
          override def notify(screen: Seq[String]): MockUserState =
            fail("Received screen after game should be over")

          override def notifyGameOver(score: Int): MockUserState =
            fail("Received game over message twice")

          override def seenFirstScreen: Boolean = true

          override def seenGameOverScreen: Boolean = true
        }

        private var state: MockUserState = StateOne: MockUserState

        override def notify(screen: Seq[String]): Unit = state = state.notify(screen)

        def seenFirstScreen: Boolean = state.seenFirstScreen

        def seenGameOverScreen: Boolean = state.seenGameOverScreen

        /** Notify the observer that the game is over
         *
         * @param score The final score */
        override def notifyGameOver(score: Int): Unit = state = state.notifyGameOver(score)
      }

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