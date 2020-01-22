package mock

import gamedata.{Fact, pInventory}
import org.scalatest.Assertions
import rogue.Command
import view.IView

/** A mock object that implements the [[IView]] interface, thus simulating a user playing a transparent game of
 * Rogue.
 *
 * An instance of [[MockUser]] should be constructed using the companion object, which provides a fluid interface.
 * For example, the following creates a mock user who expects to see firstScreen and firstInventory, will enter
 * a command to go up, then expect the game to end with a score of 10;
 *
 * MockUser.Start
 * .pCommand(firstScreen, firstInventory, pCommand.UP)
 * .GameOver(10) 
 *
 * The implementation of [[MockUser]] uses the State pattern. */
class MockUser(initial: MockUserState) extends IView with Assertions {
  override def displayFact(fact: Fact): Unit = state = state.displayFact(fact)

  private var state: MockUserState = initial

  /** True if the mock user is in the TERMINAL State */
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

/** A state that a [[MockUser]] object may be in */
private trait MockUserState extends Assertions {
  def displayFact(fact: Fact): MockUserState = fail(s"Unexpected fact: $fact")

  val finished: Boolean = false

  def displayError(err: String): MockUserState = fail(s"ERROR: $err")

  def displayGameOver(finalScore: Int): MockUserState = fail("Displayed game over")

  def getCommand: (Command, MockUserState) = fail(s"Asked for command in state $this")

  def displayScreen(screen: String): MockUserState = fail(s"Unexpected screen: $screen")

  def displayInventory(inventory: pInventory): MockUserState = fail(s"Unexpected inventory: $inventory")
}

private object MockUserState {

  /** A mock user that will wait until they have seen [[expectedScreen]] and [[expectedInventory]], then enter [[command]]
   * and switch to state [[next]].
   *
   * @param displayedScreen    True if the user has seen expectedScreen
   * @param displayedInventory True if the user has seen expectedInventory */
  class Command(expectedScreen: String,
                expectedInventory: pInventory,
                expectedKnowledge: Set[Fact],
                command: rogue.Command,
                displayedScreen: Boolean,
                displayedInventory: Boolean,
                displayedFacts: Set[Fact],
                next: MockUserState) extends MockUserState {
    override def displayScreen(screen: String): MockUserState =
      if (screen == expectedScreen) new Command(expectedScreen, expectedInventory, expectedKnowledge, command, true, displayedInventory, displayedFacts, next)
      else fail(s"Unexpected screen:\n$screen\nExpected screen:$expectedScreen")

    override def displayInventory(inventory: pInventory): MockUserState =
      if (inventory == expectedInventory) new Command(expectedScreen, expectedInventory, expectedKnowledge, command, displayedScreen, true, displayedFacts, next)
      else fail(s"Unexpected inventory:\n$inventory\nExpected inventory:\n$expectedInventory")

    override def displayFact(fact: Fact): MockUserState =
      new Command(expectedScreen, expectedInventory, expectedKnowledge, command, displayedScreen, displayedInventory, displayedFacts + fact, next)

    override def getCommand: (rogue.Command, MockUserState) = {
      if (!displayedScreen) fail(s"Screen never displayed: $expectedScreen")
      if (!displayedInventory) fail(s"Inventory never displayed: $expectedInventory")
      if (displayedFacts != expectedKnowledge) fail(s"Unexpected knowledge: $displayedFacts Expected: $expectedKnowledge")
      (command, next)
    }
  }

  object Command {
    def apply(expectedScreen: String, expectedInventory: pInventory, expectedKnowledge: Set[Fact], command: rogue.Command, next: MockUserState): Command =
      new Command(expectedScreen, expectedInventory, expectedKnowledge, command, false, false, Set(), next)
  }

  /** A mock user that will wait until they have seen the game over message with score [[expectedScore]], then switch
   * to state [[next]] */
  case class GameOver(expectedScore: Int, next: MockUserState) extends MockUserState {
    override def displayGameOver(finalScore: Int): MockUserState = {
      assert(finalScore == expectedScore)
      next
    }
  }

  /** A mock user in this state will fail if any of its methods is called. */
  case object TERMINAL extends MockUserState {
    override val finished: Boolean = true
  }

}

/** A [[MockUserBuilder]] is an object that can construct a [[MockUser]] using the method build. We think of it as
 * an incomplete set of instructions for a [[MockUser]], waiting to have more instructions added after the end.
 *
 * As an example, consider the mock user
 * MockUser.Start.pCommand(firstScreen, firstInventory, pCommand.RIGHT)
 *
 * This represents the following incomplete set of instructions:
 *
 * Wait until you have seen the screen firstScreen and firstInventory.
 * Perform the command RIGHT.
 *
 * The methods of this [[MockUserBuilder]] object provide ways to extend this set of instructions.
 *
 * We can also think of it
 * as a [[MockUser]] with a hole. When a [[MockUserState]] is plugged into the hole, the result is a complete [[MockUser]].
 *
 * As an example, consider again the mock user
 * MockUser.Start.pCommand(firstScreen, firstInventory, pCommand.RIGHT)
 *
 * This represents the following "[[MockUser]] with a hole":
 *
 * "Wait until you have seen the screen firstScreen and the inventory firstInventory, perform the command RIGHT,
 * and then switch to state (hole)."
 * */
trait MockUserBuilder {
  outer =>
  /** Plug the given mockUserState into the hole and return the [[MockUser]] constructed. */
  def build(mockUserState: MockUserState): MockUser

  /** Add the following instructions to the list:
   *
   * Wait until you have seen [[expectedScreen]] and [[expectedInventory]].
   * Perform the command [[command]] */
  class Command(expectedScreen: String, expectedInventory: pInventory, expectedKnowledge: Set[Fact], command: rogue.Command) extends MockUserBuilder {
    override def build(mockUserState: MockUserState): MockUser =
      outer.build(MockUserState.Command(expectedScreen, expectedInventory, expectedKnowledge, command, mockUserState))
  }

  def Command(expectedScreen: String, expectedInventory: pInventory, expectedKnowledge: Set[Fact], command: rogue.Command): MockUserBuilder =
    new Command(expectedScreen, expectedInventory, expectedKnowledge, command)

  /** Add the following instructions to the list.
   *
   * Wait until you have seen a game over message with the score [[expectedScore]]. */
  class GameOver(expectedScore: Int) extends MockUserBuilder {
    override def build(mockUserState: MockUserState): MockUser =
      outer.build(MockUserState.GameOver(expectedScore, mockUserState))
  }

  def GameOver(expectedScore: Int): MockUserBuilder = new GameOver(expectedScore)

  /** Mark the list of instructions as finished, and return the corresponding [[MockUser]] object. */
  def End: MockUser = build(MockUserState.TERMINAL)
}

object MockUser {

  /** A [[MockUserBuilder]] with an empty list of instructions. */
  object Start extends MockUserBuilder {
    override def build(mockUserState: MockUserState): MockUser = new MockUser(mockUserState)
  }

}

