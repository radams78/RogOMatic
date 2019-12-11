package acceptance

import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Acceptance tests for playing Rogue in transparent mode */
class RoguePlayerSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = OneMoveGame.oneMoveGame
      val player: NotStarted = RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      val p2: GameOn = player.start()

      Then("the first screen should be displayed")
      p2.getScreen should be(OneMoveGame.firstScreen)

      And("the first inventory should be displayed")
      p2.getInventory should be(Right(OneMoveGame.firstInventory))

      And("the game should not be over")
      p2.gameOver should be(false)

      When("the user enters the command to go right")
      val p3: RoguePlayer = p2.sendCommand(Command.RIGHT)

      p3 match {
        case p: GameOn =>
          Then("the second screen should be displayed")
          p.getScreen should be(OneMoveGame.secondScreen)

          And("the inventory should be displayed")
          p.getInventory should be(Right(OneMoveGame.firstInventory))
        case p: GameOver => fail("Game ended prematurely")
      }
    }

    Scenario("User plays a game of Rogue in transparent mode and is killed") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = DeathGame.deathGame
      val player: NotStarted = RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      val p2: GameOn = player.start()

      And("the PC is killed")
      val p3: RoguePlayer = p2.sendCommand(Command.REST)

      Then("the game should be over")
      p3.gameOver should be(true)
    }

    Scenario("Rogue displays a -more- message") {
      Given("a game of Rogue in progress")
      val rogue: MockRogue = MoreGame.moreGame
      val player: GameOn = new GameOn(rogue)

      When("the user enters a command to which Rogue responds with -more-")
      val p2: RoguePlayer = player.sendCommand(Command.RIGHT)

      Then("the final screen should be displayed")
      p2 match {
        case p: GameOn => p.getScreen should be(MoreGame.thirdScreen)
        case _: GameOver => fail("Game ended prematurely")
      }
    }
  }
}

