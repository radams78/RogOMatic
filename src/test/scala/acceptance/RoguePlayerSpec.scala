package acceptance

import gamedata.{Scroll, ScrollPower, Slot}
import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue.RoguePlayer.GameOn
import rogue._

/** Acceptance tests for playing Rogue in transparent mode */
class RoguePlayerSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  import EitherAssertion._

  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = TestGame.testGame
      val player: RoguePlayer.NotStarted = RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      val p2: RoguePlayer.GameOn = player.start()

      Then("the first screen should be displayed")
      p2.getScreen should be(TestGame.firstScreen)

      And("the first inventory should be displayed")
      p2.getInventory should be(Right(TestGame.firstInventory))

      When("the user enters the command to go right")
      val p3: GameOn = getGameOn(p2.sendCommand(Command.RIGHT))

      Then("the second screen should be displayed")
      p3.getScreen should be(TestGame.secondScreen)

      And("the inventory should be displayed")
      p3.getInventory should be(Right(TestGame.firstInventory))

      When("the user enters the command te read a scroll")
      val p4: GameOn = getGameOn(p3.sendCommand(Command.Read(Slot.F, Scroll(1, "coph rech"))))

      Then("the final screen should be displayed")
      p4.getScreen should be(TestGame.fourthScreen)

      And("the final inventory should be displayed")
      p4.getInventory should be(TestGame.fourthInventory)

      And("the scroll power should be remembered")
      p4.getPowers should be(Map("coph rech" -> ScrollPower.REMOVE_CURSE))
    }

    Scenario("User plays a game of Rogue in transparent mode and is killed") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = DeathGame.deathGame
      val player: RoguePlayer.NotStarted = RoguePlayer(rogue)

      When("the user starts the game in transparent mode")
      val p2: RoguePlayer.GameOn = player.start()

      And("the PC is killed")
      val p3: RoguePlayer = getEither(p2.sendCommand(Command.REST))

      Then("the game should be over")
      And("the final score should be shown")
      p3 match {
        case p: RoguePlayer.GameOver => p.getScore should be(7)
        case _ => fail("Game not ended when it should have")
      }
    }

    Scenario("Rogue displays a -more- message") {
      Given("a game of Rogue in progress")
      val rogue: MockRogue = MoreGame.moreGame
      val player: RoguePlayer.GameOn = new RoguePlayer.GameOn(rogue, Map()) // TODO Duplication

      When("the user enters a command to which Rogue responds with -more-")
      val p2: GameOn = getGameOn(player.sendCommand(Command.RIGHT))

      Then("the final screen should be displayed")
      p2.getScreen should be(MoreGame.thirdScreen)
    }
  }
}
