package acceptance

import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogomatic.Controller
import rogue.Command

/** Acceptance tests for playing Rogue in transparent mode */
class TransparentSpec extends AnyFeatureSpec with GivenWhenThen {
  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue2 = new MockRogue2
      val controller: Controller = Controller(rogue, MockView)

      When("the user starts the game in transparent mode")
      controller.startTransparent()

      Then("the first screen should be displayed")
      MockView.assertDisplayed(rogue.firstScreen)

      And("the first inventory should be displayed")
      MockView.assertDisplayedInventory(rogue.firstInventory)

      And("the game should not be over")
      assert(!controller.gameOver)

      When("the user enters the command to go right")
      MockView.resetDisplay()
      MockView.resetDisplayedInventory()
      controller.sendCommand(Command.RIGHT)

      Then("the second screen should be displayed")
      MockView.assertDisplayed(rogue.secondScreen)

      And("the inventory should be displayed")
      MockView.assertDisplayedInventory(rogue.firstInventory)
    }
  }
}