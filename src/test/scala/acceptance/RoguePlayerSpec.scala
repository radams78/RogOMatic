package acceptance

import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, RoguePlayer}

/** Acceptance tests for playing Rogue in transparent mode */
class RoguePlayerSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = OneMoveGame.oneMoveGame
      val player: RoguePlayer = new RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      player.start()

      Then("the first screen should be displayed")
      player.getScreen should be(OneMoveGame.firstScreen)

      And("the first inventory should be displayed")
      player.getInventory should be(Right(OneMoveGame.firstInventory))

      And("the game should not be over")
      player.gameOver should be(false)

      When("the user enters the command to go right")
      player.sendCommand(Command.RIGHT)

      Then("the second screen should be displayed")
      player.getScreen should be(OneMoveGame.secondScreen)

      And("the inventory should be displayed")
      player.getInventory should be(Right(OneMoveGame.firstInventory))
    }

    Scenario("User plays a game of Rogue in transparent mode and is killed") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = DeathGame.deathGame
      val player: RoguePlayer = new RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      player.start()

      And("the PC is killed")
      player.sendCommand(Command.REST)

      Then("the game should be over")
      player.gameOver should be(true)
    }
  }
}