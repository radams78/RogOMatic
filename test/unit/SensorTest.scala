package unit

import model.gamedata.{Arrows, Food, Inventory, InventoryParser, Mace, RingMail, ShortBow, Slot}
import model.{IGameOverObserver, IInventoryObserver, IScoreObserver, Sensor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.rogue.{IRogue, IScreenObserver, Screen}

class SensorTest extends AnyFlatSpec with Matchers {
  "A sensor" should "report the final score" in {
    val screenContents: String =
      """quit with 20 gold-more-
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
        |                                                      ----------------+--
        |                                                      |.................|
        |                                                      |.................|
        |                                                      |.................|
        |                                                      |..@..............|
        |                                                      +.................|
        |                                                      -------------------
        |Level: 1  Gold: 20      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0""".stripMargin

    val screen: Screen = Screen.makeScreen(screenContents)

    object MockRogue extends IRogue {
      private var _observer : Option[IScreenObserver] = None
      
      override def sendKeypress(keypress: Char): Unit = 
        if (keypress == 'Q')
          for (observer <- _observer)
            observer.notify(screen)

      override def startGame(): Unit = ()

      override def addScreenObserver(observer: IScreenObserver): Unit = _observer = Some(observer)
    }
    
    object MockObserver extends IScoreObserver {
      private var _seenScore: Boolean = false

      def seenScore: Boolean = _seenScore

      override def notifyScore(score: Int): Unit = {
        score should be(20)
        _seenScore = true
      }
    }

    val sensor: Sensor = new Sensor(MockRogue, InventoryParser)
    sensor.addScoreObserver(MockObserver)
    MockRogue.sendKeypress('Q')
    MockObserver should be(Symbol("seenScore"))
  }

  "A sensor" should "recognize a game over screen" in {
    val screenContents: String =
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
        | 1         0   robin: quit on level 1
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

    val screen: Screen = Screen.makeScreen(screenContents)

    object MockRogue extends IRogue {
      private var _observer : Option[IScreenObserver] = None
      
      override def sendKeypress(keypress: Char): Unit =
        if (keypress == ' ')
          for (observer <- _observer)
            observer.notify(screen)

      override def startGame(): Unit = ()

      override def addScreenObserver(observer: IScreenObserver): Unit = _observer = Some(observer)
    }
    
    object MockObserver extends IGameOverObserver {
      private var _seenGameOverScreen: Boolean = false

      def seenGameOverScreen: Boolean = _seenGameOverScreen

      override def notifyGameOver(): Unit = {
        _seenGameOverScreen = true
      }
    }

    val sensor: Sensor = new Sensor(MockRogue, InventoryParser)
    sensor.addGameOverObserver(MockObserver)
    MockRogue.sendKeypress(' ')
    MockObserver should be(Symbol("seenGameOverScreen"))
  }
  
  "A sensor" should "call up the inventory" in {
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

        override def transitions: Map[Char, MockRogueState] = Map('i' -> StateTwo)
      }

      private object StateTwo extends MockRogueState {
        override def readScreen: Option[Screen] = Some(screen2)

        override def transitions: Map[Char, MockRogueState] = Map(' ' -> StateOne)
      }

      private var state: MockRogueState = StateOne

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
    
    object MockInventoryObserver extends IInventoryObserver {
      private var _seenInventory : Boolean = false
      def seenInventory : Boolean = _seenInventory
      override def notify(inventory: Inventory): Unit = {
        inventory should be(inventory1)
        _seenInventory = true
      }
    }
    
    val sensor: Sensor = new Sensor(MockRogue, InventoryParser)
    sensor.addInventoryObserver(MockInventoryObserver)
    MockRogue.startGame()
    MockInventoryObserver should be(Symbol("seenInventory"))
  }
}
