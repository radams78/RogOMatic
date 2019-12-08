package mock

import org.scalatest.Assertions
import rogue.IRogue

/** Mock Rogue object. Uses State pattern */
class MockRogue(initialState: MockRogueState) extends IRogue with Assertions {
  private var state: MockRogueState = initialState

  final override def start(): Unit = state = state.start()

  final override def getScreen: String = state.getScreen

  final override def sendKeypress(keypress: Char): Unit = state = state.sendKeypress(keypress)

  final def isStarted: Boolean = state.isStarted
}

object MockRogue {

  case object Start extends MockRogueBuilder {
    override def build(mockRogueState: MockRogueState): MockRogueState = MockRogueState.Initial(mockRogueState)
  }

}

trait MockRogueState extends Assertions {
  def isStarted: Boolean

  def sendKeypress(keypress: Char): MockRogueState

  def getScreen: String

  def start(): MockRogueState
}

object MockRogueState {

  trait Started extends MockRogueState {
    final override def isStarted: Boolean = true

    final override def sendKeypress(keypress: Char): MockRogueState = transitions.lift(keypress)
      .getOrElse(fail(s"Unexpected keypress: $keypress\n$getScreen"))

    final override def start(): MockRogueState = fail(s"start() called after game started\n$getScreen")

    def transitions: PartialFunction[Char, MockRogueState]
  }

  trait ScreenInventoryPair {
    outer =>
    def screen: String

    def inventoryScreen: String

    def transitions: PartialFunction[Char, MockRogueState]

    final object Screen extends Started {
      override def transitions: PartialFunction[Char, MockRogueState] = outer.transitions.orElse({
        case 'i' => InventoryScreen
      })

      override def getScreen: String = screen
    }

    final object InventoryScreen extends Started {
      override def getScreen: String = inventoryScreen

      override def transitions: PartialFunction[Char, MockRogueState] = {
        case ' ' => Screen
      }
    }

  }

  case class Terminal(screen: String, inventoryScreen: String) extends ScreenInventoryPair {
    override def transitions: PartialFunction[Char, MockRogueState] = PartialFunction.empty
  }

  case class WaitForCommand(screen: String, inventoryScreen: String, command: Char, next: MockRogueState) extends ScreenInventoryPair {
    override def transitions: PartialFunction[Char, MockRogueState] = {
      case k if k == command => next
    }
  }

  case class Initial(next: MockRogueState) extends MockRogueState {
    override def sendKeypress(keypress: Char): MockRogueState =
      fail(s"keypress sent before game started:$keypress")

    override def getScreen: String = fail("getScreen called before game started")

    override def start(): MockRogueState = next

    override def isStarted: Boolean = false
  }

}

trait MockRogueBuilder {
  outer =>
  def build(mockRogueState: MockRogueState): MockRogueState

  final case class WaitForCommand(screen: String, inventoryScreen: String, command: Char) extends MockRogueBuilder {
    override def build(mockRogueState: MockRogueState): MockRogueState =
      outer.build(MockRogueState.WaitForCommand(screen, inventoryScreen, command, mockRogueState).Screen)
  }

  final case class End(screen: String, inventoryScreen: String)
    extends MockRogue(build(MockRogueState.Terminal(screen, inventoryScreen).Screen))

}