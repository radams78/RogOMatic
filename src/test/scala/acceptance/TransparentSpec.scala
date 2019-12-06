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
      val controller: Controller = Controller(MockRogue2, MockView)

      When("the user starts the game in transparent mode")
      controller.startTransparent()

      Then("the first screen should be displayed")
      MockView.assertDisplayed(MockRogue2.firstScreen)

      And("the first inventory should be displayed")
      MockView.assertDisplayedInventory(MockRogue2.firstInventory)

      When("the user enters the command to go right")
      controller.sendCommand(Command.RIGHT)

      Then("the second screen should be displayed")
      MockView.assertDisplayed(MockRogue2.secondScreen)

      And("the inventory should be displayed")
      MockView.assertDisplayedInventory(MockRogue2.firstInventory)
    }
  }
}