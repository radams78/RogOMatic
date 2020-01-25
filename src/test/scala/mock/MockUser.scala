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
  private var state: MockUserState = initial

  /** True if the mock user is in the TERMINAL State */
  def finished: Boolean = state.finished

  override def displayError(err: String): Unit = fail(err)

  override def displayFact(fact: Fact): Unit = state = state.display(Displayable.Fact(fact))

  override def displayGameOver(finalScore: Int): Unit = state = state.display(Displayable.GameOver(finalScore))

  override def displayInventory(inventory: pInventory): Unit = state = state.display(Displayable.Inventory(inventory))

  override def displayScreen(screen: String): Unit = state = state.display(Displayable.Screen(screen))

  override def getCommand: Command = state.getCommand match {
    case (cmd, newState) =>
      state = newState
      cmd
  }
}

// TODO Move this outside test suite?
private trait Displayable

private object Displayable {

  case class GameOver(finalScore: Int) extends Displayable

  case class Inventory(inventory: pInventory) extends Displayable

  case class Screen(screen: String) extends Displayable

  case class Fact(fact: gamedata.Fact) extends Displayable

}

/** A state that a [[MockUser]] object may be in */
private trait MockUserState extends Assertions {
  def name: String

  def display(displayable: Displayable): MockUserState = fail(s"Unexpected data to display in state $name: $displayable")

  val finished: Boolean = false

  def getCommand: (Command, MockUserState) = fail(s"Asked for command in state $name")
}

private object MockUserState {

  /** A mock user that will wait until they have seen [[expected]], then enter [[command]]
   * and switch to state [[next]]. */
  class Command(expected: Set[Displayable],
                command: rogue.Command,
                next: MockUserState) extends MockUserState {
    override def display(displayable: Displayable): MockUserState =
      if (expected contains displayable) new Command(expected - displayable, command, next)
      else fail(s"Unexpected data to display in state $name: $displayable")

    override def getCommand: (rogue.Command, MockUserState) =
      if (expected.isEmpty) (command, next)
      else fail(s"Never displayed in state $name: $expected")

    override def name: String = s"Waiting to perform $command"
  }

  object Command {
    def apply(expectedScreen: String, expectedInventory: pInventory, expectedKnowledge: Set[Fact], command: rogue.Command, next: MockUserState): MockUserState.Command =
      new MockUserState.Command(Set(Displayable.Screen(expectedScreen), Displayable.Inventory(expectedInventory)) ++ expectedKnowledge.map(Displayable.Fact),
        command, next)
  }

  /** A mock user that will wait until they have seen the game over message with score [[expectedScore]], then switch
   * to state [[next]] */
  case class GameOver(expectedScore: Int, next: MockUserState) extends MockUserState {
    override def display(displayable: Displayable): MockUserState = displayable match {
      case Displayable.GameOver(finalScore) =>
        assert(finalScore == expectedScore)
        next
      case _ => fail(s"Unexpected data to display in state $name: $displayable")
    }

    override def name: String = "Game over"
  }

  /** A mock user in this state will fail if any of its methods is called. */
  case object TERMINAL extends MockUserState {
    override val finished: Boolean = true

    override def name: String = "terminal"
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

