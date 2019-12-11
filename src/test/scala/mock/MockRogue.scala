package mock

import org.scalatest.Assertions
import rogue.IRogue

/** Mock Rogue object. Uses State pattern */
class MockRogue(initialState: MockRogueState) extends IRogue with Assertions {
  private var state: MockRogueState = initialState

  final override def start(): Unit = state = state.start()

  final override def getScreen: String = state.getScreen

  final override def sendKeypress(keypress: Char): Unit = state = state.sendKeypress(keypress)

  /** True if the game of Rogue has started */
  final def isStarted: Boolean = state.isStarted
}

object MockRogue {
  /** Take a string and pad it out so that it has lines of length 80 */
  def makeScreen(screen: String): String = screen.split("\n").padTo(24, "").map(_.padTo(80, ' ')).mkString("\n")

  /** Initial command in the DSL for building [[MockRogue]] objects */
  case object Start extends MockRogueBuilder {
    override def build(mockRogueState: MockRogueState): MockRogueState = MockRogueState.Initial(mockRogueState)
  }

}

/** The state of a [[MockRogue]] object */
trait MockRogueState extends Assertions {
  /** True if the game of Rogue has started */
  def isStarted: Boolean

  /** Given a keypress sent to the game of Rogue, returns the state that the [[MockRogue]] object should switch to,
   * or fails the test if that keypress is not expected */
  def sendKeypress(keypress: Char): MockRogueState

  /** Screen currently displayed by Rogue */
  def getScreen: String

  /** Start hte game of Rogue. Returns the state that the [[MockRogue]] object should switch to, or fails the test
   * if a call to start() is not expected. */
  def start(): MockRogueState
}

object MockRogueState {

  /** The state of a [[MockRogue]] object after the game of Rogue has started */
  trait Started extends MockRogueState {
    final override def isStarted: Boolean = true

    final override def sendKeypress(keypress: Char): MockRogueState = transitions.lift(keypress)
      .getOrElse(fail(s"Unexpected keypress: $keypress\n$getScreen"))

    final override def start(): MockRogueState = fail(s"start() called after game started\n$getScreen")

    /** A partial function which takes a character, and returns the state that the [[MockRogue]] should switch to
     * if that keypress is sent to Rogue. */
    def transitions: PartialFunction[Char, MockRogueState]
  }

  /** A pair of [[MockRogueState]]s (Screen, InventoryScreen) representing Rogue waiting for the next command;
   * and Rogue displaying the inventory. We can switch between the two using 'i' and ' '. */
  trait ScreenInventoryPair {
    outer =>
    /** Screen displayed when waiting for next command */
    def screen: String

    /** Screen with displayed inventory */
    def inventoryScreen: String

    /** Partial function giving the transitions that are possible from Screen, not including 'i' */
    def transitions: PartialFunction[Char, MockRogueState]

    /** State representing Rogue waiting for the next command */
    final object Screen extends Started {
      override def transitions: PartialFunction[Char, MockRogueState] = outer.transitions.orElse({
        case 'i' => InventoryScreen
      })

      override def getScreen: String = screen
    }

    /** State representing Rogue displaying the inventory */
    final object InventoryScreen extends Started {
      override def getScreen: String = inventoryScreen

      override def transitions: PartialFunction[Char, MockRogueState] = {
        case ' ' => Screen
      }
    }
  }

  /** A [[ScreenInventoryPair]] with no further transitions possible */
  case class Terminal(screen: String, inventoryScreen: String) extends ScreenInventoryPair {
    override def transitions: PartialFunction[Char, MockRogueState] = PartialFunction.empty
  }

  /** A [[ScreenInventoryPair]] with one further transation: the keiypress 'command' will switch to state 'next' */
  case class WaitForCommand(screen: String, inventoryScreen: String, command: Char, next: MockRogueState) extends ScreenInventoryPair {
    override def transitions: PartialFunction[Char, MockRogueState] = {
      case k if k == command => next
    }
  }

  /** A state representing a Rogue process waiting for start() to be called, after which it switches to 'next' */
  case class Initial(next: MockRogueState) extends MockRogueState {
    override def sendKeypress(keypress: Char): MockRogueState =
      fail(s"keypress sent before game started:$keypress")

    override def getScreen: String = fail("getScreen called before game started")

    override def start(): MockRogueState = next

    override def isStarted: Boolean = false
  }

  case object Ended extends MockRogueState {
    override def isStarted: Boolean = false

    override def sendKeypress(keypress: Char): MockRogueState = fail("sendKeypress called after Rogue process ends")

    override def getScreen: String = fail("getScreen called after Rogue process ends")

    override def start(): MockRogueState = fail("start() called after Rogue process ends")
  }

}

/** A partial [[MockRogueState]] - a [[MockRogueState]] with a hole. */
trait MockRogueBuilder {
  outer =>
  /** Fill in the hole with the given [[MockRogueState]] */
  def build(mockRogueState: MockRogueState): MockRogueState

  /** Display screen and inventoryScreen until command is received, then switch to state (hole) */
  final case class WaitForCommand(screen: String, inventoryScreen: String, command: Char) extends MockRogueBuilder {
    override def build(mockRogueState: MockRogueState): MockRogueState =
      outer.build(MockRogueState.WaitForCommand(screen, inventoryScreen, command, mockRogueState).Screen)
  }

  /** Fill in the hole with a state that displays screen and inventoryScreen */
  final case class End(screen: String, inventoryScreen: String)
    extends MockRogue(build(MockRogueState.Terminal(screen, inventoryScreen).Screen))

}