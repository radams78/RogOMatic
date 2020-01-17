package mock

import gamedata.item.magic.scroll.ScrollPower
import gamedata.item.magic.scroll.ScrollPower.ScrollPower
import gamedata.{Fact, Slot, pInventory}
import org.scalatest.Assertions
import rogue.Command
import view.IView

class MockUser(initial: MockUserState) extends IView with Assertions {
  override def displayFact(fact: Fact): Unit = state = state.displayFact(fact)

  private var state: MockUserState = initial

  def finished: Boolean = state.finished

  override def displayGameOver(finalScore: Int): Unit = state = state.displayGameOver(finalScore)

  override def displayError(err: String): Unit = state = state.displayError(err)

  override def displayInventory(inventory: pInventory): Unit = state = state.displayInventory(inventory)

  override def displayScreen(screen: String): Unit = state = state.displayScreen(screen)

  override def getCommand: Command = state.getCommand match {
    case (cmd, newState) =>
      state = newState
      cmd
  }
}

private trait MockUserState extends Assertions {
  def displayFact(fact: Fact): MockUserState = fail(s"Unexpected fact: $fact")

  val finished: Boolean = false

  def displayError(err: String): MockUserState = fail(s"ERROR: $err")

  def displayGameOver(finalScore: Int): MockUserState = fail("Displayed game over")

  def getCommand: (Command, MockUserState) = fail(s"Asked for command in state $this")

  def displayScreen(screen: String): MockUserState = fail(s"Unexpected screen: $screen")

  def displayInventory(inventory: pInventory): MockUserState = fail(s"Unexpected inventory: $inventory")

  def displayPower(title: String, power: ScrollPower): MockUserState = fail(s"Unexpected scroll power: $title and $power")
}

private object MockUserState {

  class Command(expectedScreen: String,
                expectedInventory: pInventory,
                command: rogue.Command,
                displayedScreen: Boolean,
                displayedInventory: Boolean,
                next: MockUserState) extends MockUserState {
    override def displayScreen(screen: String): MockUserState =
      if (screen == expectedScreen) new Command(expectedScreen, expectedInventory, command, true, displayedInventory, next)
      else fail(s"Unexpected screen:\n$screen\nExpected screen:$expectedScreen")

    override def displayInventory(inventory: pInventory): MockUserState =
      if (inventory == expectedInventory) new Command(expectedScreen, expectedInventory, command, displayedScreen, true, next)
      else fail(s"Unexpected inventory:\n$inventory\nExpected inventory:\n$expectedInventory")

    override def getCommand: (rogue.Command, MockUserState) = {
      if (!displayedScreen) fail(s"Screen never displayed: $expectedScreen")
      if (!displayedInventory) fail(s"Inventory never displayed: $expectedInventory")
      (command, next)
    }
  }

  object Command {
    def apply(expectedScreen: String, expectedInventory: pInventory, command: rogue.Command, next: MockUserState): Command =
      new Command(expectedScreen, expectedInventory, command, false, false, next)
  }

  case class GameOver(expectedScore: Int) extends MockUserState {
    override def displayGameOver(finalScore: Int): MockUserState = {
      assert(finalScore == expectedScore)
      TERMINAL
    }
  }

  case object TERMINAL extends MockUserState {
    override val finished: Boolean = true
  }

}

trait MockUserBuilder {
  def build(mockUserState: MockUserState): MockUser

  case class Command(expectedScreen: String, expectedInventory: pInventory, command: rogue.Command) extends MockUserBuilder {
    outer =>
    override def build(mockUserState: MockUserState): MockUser = outer.build(MockUserState.Command(expectedScreen, expectedInventory, command, mockUserState))
  }

  def GameOver(expectedScore: Int): MockUser = build(MockUserState.GameOver(expectedScore))
}

object MockUser {

  object Start extends MockUserBuilder {
    override def build(mockUserState: MockUserState): MockUser = new MockUser(mockUserState)
  }

}


private class Initial(displayedScreen: Boolean, displayedInventory: Boolean) extends MockUserState {
  override def displayScreen(screen: String): MockUserState = {
    if (screen != TestGame.firstScreen) fail(s"Unexpected screen: $screen")
    else Initial(displayedScreen = true, displayedInventory = displayedInventory)
  }

  override def displayInventory(inventory: pInventory): MockUserState = {
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

    override def displayInventory(inventory: pInventory): MockUserState =
      if (inventory != TestGame.firstInventory) fail(s"Unexpected inventory: $inventory")
      else SecondScreen(displayedScreen, displayedInventory = true)
  }

  private class FourthScreen(displayedScreen: Boolean, displayedInventory: Boolean, displayedPower: Boolean) extends MockUserState {
    override def displayScreen(screen: String): MockUserState =
      if (screen != TestGame.fourthScreen) fail(s"Unexpected screen: $screen")
      else FourthScreen(displayedScreen = true, displayedInventory = displayedInventory, displayedPower = displayedPower)

    override def displayInventory(inventory: pInventory): MockUserState =
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
      if (displayedScreen && displayedInventory && displayedPower) THIRD_COMMAND
      else new FourthScreen(displayedScreen, displayedInventory, displayedPower)
  }

  private object THIRD_COMMAND extends MockUserState {
    override def getCommand: (Command, MockUserState) = (Command.LEFT, FifthScreen)
  }

  private class FifthScreen(displayedScreen: Boolean, displayedInventory: Boolean) extends MockUserState {
    override def getCommand: (Command, MockUserState) = fail(s"Asked for command while\n"
      + (if (displayedScreen) "" else "screen not displayed")
      + (if (displayedInventory) "" else "inventory not displayed"))

    override def displayScreen(screen: String): MockUserState =
      if (screen != TestGame.secondScreen) fail(s"Unexpected screen: $screen")
      else SecondScreen(displayedScreen = true, displayedInventory = displayedInventory)

    override def displayInventory(inventory: pInventory): MockUserState =
      if (inventory != TestGame.firstInventory) fail(s"Unexpected inventory: $inventory")
      else SecondScreen(displayedScreen, displayedInventory = true)
  }

  private object FifthScreen extends MockUserState {
    override def displayGameOver(finalScore: Int): MockUserState = {
      assert(finalScore == 0)
      FinalState
    }
  }

  private object FinalState extends MockUserState {
    override val finished: Boolean = true
  }

