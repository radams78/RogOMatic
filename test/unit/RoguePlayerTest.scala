package unit

import model.items.Inventory
import model.rogue.{IRogue, Screen}
import model.{IInventoryObserver, IScreenObserver, RoguePlayer, ScreenReader}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RoguePlayerTest extends AnyFlatSpec with Matchers {
  def fixture: Object {
    val screen: Screen

    val rogue: IRogue

    val screenReader: ScreenReader
  } = new {
    val screen: Screen = Screen.makeScreen(
      """
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
        |
        |
        |
        |  -------+---
        |  |.@.......|
        |  |.........|
        |  |.........|
        |  |........*|
        |  -----------
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
    )

    val rogue : IRogue = MockRogue

    val screenReader: ScreenReader = new ScreenReader
    
    object MockRogue extends IRogue {
      private var _started: Boolean = false

      def started: Boolean = _started

      override def sendKeypress(keypress: Char): Unit = ()

      override def startGame(): Unit = {
        _started = true
        screenReader.notify(screen)
      }
    }
  }

  "A Rogue player" should "start the game of Rogue" in {
    val f: Object {
      val screen: Screen

      val rogue: IRogue

      val screenReader: ScreenReader
    } = fixture
    
    val player : RoguePlayer = new RoguePlayer(f.rogue, f.screenReader)
    player.startGame()
    f.rogue should be(Symbol("started"))
  }
  
  "A Rogue player" should "broadcast the screen from Rogue" in {
    val f: Object {
      val screen: Screen

      val rogue: IRogue

      val screenReader: ScreenReader
    } = fixture
    
    object MockObserver extends IScreenObserver {
      private var _seenScreen: Boolean = false
      
      def seenScreen: Boolean = _seenScreen
      
      override def notify(_screen: Screen): Unit = {
        _screen should be(f.screen)
        _seenScreen = true
      }
    }
    
    val player : RoguePlayer = new RoguePlayer(f.rogue, f.screenReader)
    f.screenReader.addScreenObserver(MockObserver)
    player.startGame()
    MockObserver should be(Symbol("seenScreen"))
  }
  
  "A Rogue player" should "broadcast the inventory from Rogue" in {
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
      """                                                --press space to continue--
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
    
    val screenReader : ScreenReader = new ScreenReader
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

      override def startGame(): Unit = 
        for (screen <- state.readScreen)
          screenReader.notify(screen)
    }

    object MockObserver extends IInventoryObserver {
      private var _seenInventory: Boolean = false
      
      def seenInventory: Boolean = _seenInventory
      
      override def notify(inventory: Inventory): Unit = {
        inventory should be(Inventory())
        _seenInventory = true
      }
    }
    
    val player: RoguePlayer = new RoguePlayer(MockRogue, screenReader)
    player.addInventoryObserver(MockObserver)
    player.startGame()
    MockObserver should be(Symbol("seenInventory"))
  }
}
