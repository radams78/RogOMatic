package acceptance

import gamedata._
import gamedata.items.ScrollPower
import gamedata.items.ScrollPower.ScrollPower
import mock._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertions, GivenWhenThen}
import rogomatic.RogOMatic
import rogue._
import view.IView

class MockUser extends IView with Assertions {
  private var state: MockUserState = Initial()

  def finished: Boolean = state.finished

  override def displayGameOver(finalScore: Int): Unit = state = state.displayGameOver(finalScore)

  override def displayError(err: String): Unit = state = state.displayError(err)

  override def displayInventory(inventory: Inventory): Unit = state = state.displayInventory(inventory)

  override def displayScreen(screen: String): Unit = state = state.displayScreen(screen)

  override def getCommand: Command = state.getCommand match {
    case (cmd, newState) =>
      state = newState
      cmd
  }

  private trait MockUserState {
    val finished: Boolean = false

    def displayError(err: String): MockUserState = fail(s"ERROR: $err")

    def displayGameOver(finalScore: Int): MockUserState = fail("Displayed game over")

    def getCommand: (Command, MockUserState) = fail(s"Asked for command in state $state")

    def displayScreen(screen: String): MockUserState = fail(s"Unexpected screen: $screen")

    def displayInventory(inventory: Inventory): MockUserState = fail(s"Unexpected inventory: $inventory")

    def displayPower(title: String, power: ScrollPower): MockUserState = fail(s"Unexpected scroll power: $title and $power")
  }

  private class Initial(displayedScreen: Boolean, displayedInventory: Boolean) extends MockUserState {
    override def displayScreen(screen: String): MockUserState = {
      if (screen != TestGame.firstScreen) fail(s"Unexpected screen: $screen")
      else Initial(displayedScreen = true, displayedInventory = displayedInventory)
    }

    override def displayInventory(inventory: Inventory): MockUserState = {
      if (inventory != TestGame.firstInventory) fail(s"Unexpected inventory: $inventory")
      else Initial(displayedScreen, displayedInventory = true)
    }
  }

  private class SecondScreen(displayedScreen: Boolean, displayedInventory: Boolean) extends MockUserState {
    override def getCommand: (Command, MockUserState) = fail(s"Asked for command while\n"
      + (if (displayedScreen) "" else "screen not displayed")
      + (if (displayedInventory) "" else "inventory not displayed"))

    override def displayScreen(screen: String): MockUserState =
      if (screen != TestGame.secondScreen) fail(s"Unexpected screen: $screen")
      else SecondScreen(displayedScreen = true, displayedInventory = displayedInventory)

    override def displayInventory(inventory: Inventory): MockUserState =
      if (inventory != TestGame.firstInventory) fail(s"Unexpected inventory: $inventory")
      else SecondScreen(displayedScreen, displayedInventory = true)
  }

  override def displayScrollPower(title: String, power: ScrollPower): Unit = state = state.displayPower(title, power)

  private class FourthScreen(displayedScreen: Boolean, displayedInventory: Boolean, displayedPower: Boolean) extends MockUserState {
    override def displayScreen(screen: String): MockUserState =
      if (screen != TestGame.fourthScreen) fail(s"Unexpected screen: $screen")
      else FourthScreen(displayedScreen = true, displayedInventory = displayedInventory, displayedPower = displayedPower)

    override def displayInventory(inventory: Inventory): MockUserState =
      if (inventory != TestGame.fourthInventory) fail(s"Unexpected inventory: $inventory")
      else FourthScreen(displayedScreen, displayedInventory = true, displayedPower = displayedPower)

    override def displayPower(title: String, power: ScrollPower): MockUserState =
      if (title != "coph rech") fail(s"Unexpected scroll title: $title")
      else if (power != ScrollPower.REMOVE_CURSE) fail(s"Unexpected scroll power: $power")
      else FourthScreen(displayedScreen, displayedInventory, displayedPower = true)
  }

  private object Initial {
    def apply(): Initial = new Initial(false, false)

    def apply(displayedScreen: Boolean, displayedInventory: Boolean): MockUserState =
      if (displayedScreen && displayedInventory) FIRST_COMMAND else new Initial(displayedScreen, displayedInventory)
  }

  private object FIRST_COMMAND extends MockUserState {
    override def getCommand: (Command, MockUserState) = (Command.RIGHT, SecondScreen())
  }

  private object SecondScreen {
    def apply(): SecondScreen = new SecondScreen(false, false)

    def apply(displayedScreen: Boolean, displayedInventory: Boolean): MockUserState =
      if (displayedScreen && displayedInventory) SECOND_COMMAND else new SecondScreen(displayedScreen, displayedInventory)
  }

  private object SECOND_COMMAND extends MockUserState {
    override def getCommand: (Command, MockUserState) = (Command.Read(TestGame.firstInventory, Slot.F), FourthScreen())
  }

  private object FourthScreen {
    def apply(): FourthScreen = new FourthScreen(false, false, false)

    def apply(displayedScreen: Boolean, displayedInventory: Boolean, displayedPower: Boolean): MockUserState =
      if (displayedScreen && displayedInventory && displayedPower) {
        succeed
        Initial()
      } else new FourthScreen(displayedScreen, displayedInventory, displayedPower)
  }
}

/** Acceptance tests for playing Rogue in transparent mode */
class RogueActuatorSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = TestGame.testGame
      val user: MockUser = new MockUser

      When("the user starts the game in transparent mode")
      RogOMatic.playTransparentGame(rogue, user)

      Then("the first screen should be displayed")
      And("the first inventory should be displayed")
      When("the user enters the command to go right")
      Then("the second screen should be displayed")
      And("the inventory should be displayed")
      When("the user enters the command te read a scroll")
      Then("the final screen should be displayed")
      And("the final inventory should be displayed")
      And("the scroll power should be remembered")
    }

    Scenario("User plays a game of Rogue in transparent mode and is killed") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = DeathGame.deathGame
      val recorder: Recorder = new Recorder
      val player: RogueActuator = new RogueActuator(rogue, recorder)

      When("the user starts the game in transparent mode")
      player.start()
      
      And("the PC is killed")
      player.sendCommand(Command.REST)

      Then("the game should be over")
      recorder.gameOver should be(true)
      And("the final score should be shown")
      recorder.getScore should be(7)
    }

    Scenario("Rogue displays a -more- message") {
      Given("a game of Rogue in progress")
      val rogue: MockRogue = MoreGame.moreGame
      val recorder: Recorder = new Recorder
      val player: RogueActuator = new RogueActuator(rogue, recorder) // TODO Duplication

      When("the user enters a command to which Rogue responds with -more-")
      player.sendCommand(Command.RIGHT)

      Then("the final screen should be displayed")
      recorder.getScreen should be(MoreGame.thirdScreen)
    }
  }
}
