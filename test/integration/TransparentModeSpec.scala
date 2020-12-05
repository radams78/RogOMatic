package integration

import gamedata.Command
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogue.IRogue

class TransparentModeSpec extends AnyFeatureSpec with GivenWhenThen {
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

      object MockRogue extends IRogue {

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
          override def readScreen: Seq[String] =
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
              |""".stripMargin.split("\n").map(_.padTo(80, ' '))

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
        }

        private object StateThree extends MockRogueState {
          override def readScreen: Seq[String] =
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
              |""".stripMargin.split("\n").map(_.padTo(80, ' '))

          override def transitions: Map[Char, MockRogueState] = Map('y' -> StateFour)
        }

        private object StateFour extends MockRogueState {
          override def readScreen: Seq[String] =
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
              |""".stripMargin.split("\n").map(_.padTo(80, ' '))

          override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateFive)
        }

        private object StateFive extends MockRogueState {
          override def readScreen: Seq[String] =
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
              |""".stripMargin.split("\n").map(_.padTo(80, ' '))

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

        override def readScreen: Seq[String] = state.readScreen
      }

      object MockUser {

        private trait MockUserState {
          def seenGameOverScreen: Boolean = false

          def seenFirstScreen: Boolean = false

          def notify(screen: Seq[String]): MockUserState

          def notifyGameOver(score: Int): MockUserState
        }

        private object StateOne extends MockUserState {
          override def notify(screen: Seq[String]): MockUserState = if (screen == screen1.toSeq) StateTwo else
            fail("Unexpected screen:\n" + screen + "Expected:\n" +
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
            )

          override def notifyGameOver(score: Int): MockUserState =
            fail("Received game over message unexpectedly")
        }

        private object StateTwo extends MockUserState {
          override def notify(screen: Seq[String]): MockUserState =
            fail("Received screen after game should be over")

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

        def notify(screen: Seq[String]): Unit = state = state.notify(screen)

        def seenFirstScreen: Boolean = state.seenFirstScreen

        def seenGameOverScreen: Boolean = state.seenGameOverScreen
      }

      object MockView extends IView {
        override def notify(screen: Seq[String]): Unit = MockUser.notify(screen)
      }

      class MockController(player: IRoguePlayer) extends IController {
        def performCommand(command: Command): Unit = player.performCommand(command: Command)
      }

      val player : IRoguePlayer = new RoguePlayer(MockRogue)
      player.addObserver(MockView)
      val controller: IController = new MockController(player)
      player.startGame()
      
      Then("the user should see the first screen")
      assert(MockUser.seenFirstScreen)

      When("the user enters the command to quit")
      controller.performCommand(Command.QUIT)

      Then("Rogue should receive the command to quit")
      assert(MockRogue.receivedQuitCommand)

      And("the user should see the game over message")
      assert(MockUser.seenGameOverScreen)
    }

  }
}