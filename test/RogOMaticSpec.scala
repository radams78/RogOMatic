import controller.Controller
import model.{Command, RoguePlayer, RoguePlayerObserver, Screen}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogue.IRogue

class RogOMaticSpec extends AnyFeatureSpec with GivenWhenThen {
  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue and immediately quits") {
      Given("A game of Rogue")
      val firstScreen : Seq[String] =
        """
          |
          |
          |
          |
          |
          |
          |
          |                                                          --+---------------
          |                                                          |..........B.....|
          |                                                          |................|
          |                                                          |........=..%....|
          |                                                          |....@*..........|
          |                                                          |................|
          |                                                          -------+----------
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
      val secondScreen : Seq[String] =
        """really quit? 
          |
          |
          |
          |
          |
          |
          |
          |                                                          --+---------------
          |                                                          |..........B.....|
          |                                                          |................|
          |                                                          |........=..%....|
          |                                                          |....@*..........|
          |                                                          |................|
          |                                                          -------+----------
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
      val thirdScreen: Seq[String] =
        """quit with 0 gold-more-
          |
          |
          |
          |
          |
          |
          |
          |                                                          --+---------------
          |                                                          |..........B.....|
          |                                                          |................|
          |                                                          |........=..%....|
          |                                                          |....@*..........|
          |                                                          |................|
          |                                                          -------+----------
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
      val fourthScreen : Seq[String] =
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
      val screen1: Screen = new Screen()
      object MockRogue extends IRogue {
        private var state: Int = 1
        def gameOver: Boolean = false
        override def getScreen : Seq[String] = state match {
          case 1 => firstScreen
          case 2 => secondScreen
          case 3 => thirdScreen
          case 4 => fourthScreen
          case 5 => fail("getScreen called after Rogue process ended")
        }
        override def sendKeypress(keyPress: Char): Unit = {
          def expectedKeypress: Char = state match {
            case 1 => 'Q'
            case 2 => 'y'
            case 3 => ' '
            case 4 => ' '
            case 5 => fail("sendKeypress called after Rogue process ended")
          }
          if (keyPress == expectedKeypress) state += 1 else
            fail("Expected keypress: " + expectedKeypress + " Actual keypress: " + keyPress)
        }
      }
      val roguePlayer: RoguePlayer = new RoguePlayer(MockRogue)
      object MockView extends RoguePlayerObserver {
        private var displayedScreen: Option[Screen] = None
        private var gameOver: Boolean = false
        def showingGameOverMessage: Boolean = false
        def showingScreen(screen: Screen): Boolean = ! gameOver && displayedScreen.contains(screen)
        override def updateScreen(screen: Screen): Unit = displayedScreen = Some(screen)
        override def updateGameOver(): Unit = {
          displayedScreen = None
          gameOver = true
        }
      }
      roguePlayer.register(MockView)
      val controller: Controller = new Controller(roguePlayer)
      When("The user starts a game of Rogue")
      controller.startGame()
      Then("The user should see the first screen")
      assert(MockView.showingScreen(screen1))
      When("The user enters the command to quit")
      controller.sendCommand(Command.QUIT)
      Then("The game should receive the command to quit")
      assert(MockRogue.gameOver)
      And("The user should see the game over message")
      assert(MockView.showingGameOverMessage)
    }
  }
}
