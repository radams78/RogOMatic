package integration

import controller.Controller
import gamedata.Command
import main.RogOMatic
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogue.IRogue
import view.IView

class TransparentModeSpec extends AnyFeatureSpec with GivenWhenThen {
  Feature("Transparent Mode") {
    Scenario("User quits immediately") {
      Given("a new game of Rogue")
      val rogue: MockRogue = new MockRogue()
      val user: MockUser = new MockUser()
      val controller: Controller = new Controller()
      val rogomatic: RogOMatic = new RogOMatic(rogue, user)
      rogomatic.startGame()

      Then("the user should see the first screen")
      assert(user.seenFirstScreen)

      When("the user enters the command to quit")
      controller.performCommand(Command.QUIT)

      Then("Rogue should receive the command to quit")
      assert(rogue.receivedQuitCommand)

      And("the user should see the game over message")
      assert(user.seenGameOverScreen)
    }

    class MockRogue extends IRogue {

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
        override def readScreen: Seq[String] =
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
            |""".stripMargin.split("\n").map(_.padTo(80, ' '))

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

    class MockUser extends IView {

      private trait MockUserState {
        def seenGameOverScreen: Boolean = false

        def seenFirstScreen: Boolean = false

        def notify(screen: Seq[String]): MockUserState

        def notifyGameOver(score: Int): MockUserState
      }

      private object StateOne extends MockUserState {
        override def notify(screen: Seq[String]): MockUserState = if (screen ==
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
            |""".stripMargin.split("\n").map(_.padTo(80, ' ')).toSeq) StateTwo else
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

  }
}