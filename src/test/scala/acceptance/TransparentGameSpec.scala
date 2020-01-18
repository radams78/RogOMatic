package acceptance

import mock.{MockRogue, MockUser, TestGame}
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogomatic.RogOMatic

class TransparentGameSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = TestGame.testGame
      val user: MockUser = TestGame.user

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
      assert(user.finished)
    }
  }
}
