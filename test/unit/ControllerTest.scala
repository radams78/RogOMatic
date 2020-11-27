package unit

import controller.Controller
import gamedata.Command
import org.scalatest.funsuite.AnyFunSuite
import rogue.IRogue

class ControllerTest extends AnyFunSuite {
  test("Performing a command should send the command to Rogue") {
    class MockRogue extends IRogue {
      private val screen1: Array[String] =
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
        override def readScreen: Seq[String] = screen1

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

    val rogue: MockRogue = new MockRogue()
    val controller: Controller = new Controller(rogue)
    controller.performCommand(Command.QUIT)
    assert(rogue.receivedQuitCommand)
  }
}
