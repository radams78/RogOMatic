package unit

import gamedata.Command
import model.{IGameOverObserver, IRoguePlayer, IScreenObserver, RoguePlayer}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import rogue.IRogue

class RoguePlayerTest extends AnyFlatSpec {
  "A model" should "pass its first screen on to any observers" in {
    val firstScreen = Seq("The first screen")

    object MockRogue extends IRogue {
      private var _screenObserver : Option[IScreenObserver] = None

      def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)

      override def sendKeypress(keypress: Char): Unit = fail("Keypress detected")

      override def startGame(): Unit = for (observer <- _screenObserver) observer.notify(firstScreen)
    }
    
    object MockObserver extends IScreenObserver {
      def seenFirstScreen: Boolean = _seenFirstScreen

      private var _seenFirstScreen : Boolean = false

      override def notify(screen: Seq[String]): Unit = {
        screen should be(firstScreen)
        _seenFirstScreen = true
      }
    }
    
    val player : IRoguePlayer = new RoguePlayer(MockRogue)
    MockRogue.addScreenObserver(MockObserver)
    MockRogue.startGame()
    MockObserver should be(Symbol("seenFirstScreen"))
  }

  "A RoguePlayer" should "pass on commands to Rogue" in {
    object MockRogue extends IRogue {
      private var _receivedKeypress = false

      def receivedCommand(): Boolean = _receivedKeypress

      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        _receivedKeypress = true
      }

      var _screenObserver: Option[IScreenObserver] = None

      override def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)

      override def startGame(): Unit = ()
    }

    val player = new RoguePlayer(MockRogue)
    player.performCommand(Command.LEFT)
    MockRogue should be(Symbol("receivedCommand"))
  }

  "A RoguePlayer" should "read the screen after performing a command" in {
    val screen: Seq[String] = Seq("The screen")

    object MockRogue extends IRogue {
      override def sendKeypress(keypress: Char): Unit = {
        keypress should be('h')
        for (observer <- _screenObserver) observer.notify(screen)
      }

      private var _screenObserver: Option[IScreenObserver] = None

      override def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)

      override def startGame(): Unit = ()
    }

    object MockObserver extends IScreenObserver {
      private var _seenScreen = false

      def seenScreen: Boolean = _seenScreen

      /** Notify all observers that this is the screen displayed by Rogue */
      override def notify(_screen: Seq[String]): Unit = {
        _screen should be(screen)
        _seenScreen = true
      }
    }

    val player = new RoguePlayer(MockRogue)
    MockRogue.addScreenObserver(MockObserver)
    player.performCommand(Command.LEFT)
    MockObserver should be(Symbol("seenScreen"))
  }

  "A RoguePlayer" should "inform its observers when the game ends" in {
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

    val screen5lines = screen5.stripMargin.split("\n").padTo(24,"").map(_.padTo(80, ' '))

    object MockRogue extends IRogue {

      private trait MockRogueState {
        def readScreen: Seq[String]

        def transitions: Map[Char, MockRogueState]

        def receivedQuitCommand: Boolean = false

        final def sendKeypress(keypress: Char): MockRogueState = {
          transitions.getOrElse(keypress,
            fail("Unexpected keypress '" + keypress + "'")
          )
        }
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

      override def sendKeypress(keypress: Char): Unit = {
        state = state.sendKeypress(keypress)
        for (observer <- _screenObserver) observer.notify(state.readScreen)
      }

      var _screenObserver : Option[IScreenObserver] = None

      override def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)

      override def startGame(): Unit = for (observer <- _screenObserver) observer.notify(state.readScreen)
    }

    object MockObserver extends IGameOverObserver {
      private var _seenGameOverMessage = false

      def seenGameOverMessage : Boolean = _seenGameOverMessage

      override def notifyGameOver(score : Int) : Unit = {
        score should be (0)
        _seenGameOverMessage = true
      }
    }

    val player = new RoguePlayer(MockRogue)
    player.addGameOverObserver(MockObserver)
    player.performCommand(Command.QUIT)
    MockObserver should be(Symbol("seenGameOverMessage"))
  }

}
