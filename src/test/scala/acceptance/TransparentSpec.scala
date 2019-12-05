package acceptance

import mock.{MockRogue, MockView}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogomatic.Controller

/** Acceptance tests for playing Rogue in transparent mode */
class TransparentSpec extends AnyFeatureSpec with GivenWhenThen {
  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val controller: Controller = Controller(MockRogue, MockView)

      When("the user starts the game in transparent mode")
      controller.startTransparent()

      Then("the game should be started")
      assert(MockRogue.isStarted)

      And("the first screen should be displayed")
      assert(MockView.hasDisplayed(MockRogue.firstScreen))
    }
  }
}

