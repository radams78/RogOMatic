package unit

import controller.Controller
import mock.{MockRogue, MockView}
import org.scalatest.flatspec.AnyFlatSpec

/** Unit tests for [[Controller]] class */
class ControllerTest extends AnyFlatSpec {
  "A controller" should "be able to start a game of Rogue" in {
    val controller: Controller = Controller(MockRogue, MockView)
    controller.startTransparent()
    assert(MockRogue.isStarted)
  }

  "A controller" should "display the first screen of the game" in {
    val controller: Controller = Controller(MockRogue, MockView)
    controller.startTransparent()
    assert(MockView.hasDisplayed(MockRogue.firstScreen))
  }
}
