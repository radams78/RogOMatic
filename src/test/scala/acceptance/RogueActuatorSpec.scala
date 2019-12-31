package acceptance

import gamedata._
import gamedata.items.{Scroll, ScrollPower}
import gamestate.ScrollKnowledge
import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Acceptance tests for playing Rogue in transparent mode */
class RogueActuatorSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = TestGame.testGame
      val recorder: Recorder = new Recorder
      val player: RogueActuator = new RogueActuator(rogue, recorder)

      When("the user starts the game in transparent mode")
      player.start()

      Then("the first screen should be displayed")
      recorder.getScreen should be(TestGame.firstScreen)

      And("the first inventory should be displayed")
      recorder.getInventory should be(TestGame.firstInventory)

      When("the user enters the command to go right")
      player.sendCommand(Command.RIGHT)

      Then("the second screen should be displayed")
      recorder.getScreen should be(TestGame.secondScreen)

      And("the inventory should be displayed")
      recorder.getInventory should be(TestGame.firstInventory)

      When("the user enters the command te read a scroll")
      player.sendCommand(Command.Read(Slot.F, Scroll(1, "coph rech")))

      Then("the final screen should be displayed")
      recorder.getScreen should be(TestGame.fourthScreen)

      And("the final inventory should be displayed")
      recorder.getInventory should be(TestGame.fourthInventory)

      And("the scroll power should be remembered")
      recorder.getScrollKnowledge should be(new ScrollKnowledge(Map("coph rech" -> ScrollPower.REMOVE_CURSE)))
    }

    Scenario("User plays a game of Rogue in transparent mode and is killed") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = DeathGame.deathGame
      val recorder: Recorder = new Recorder
      val player: RogueActuator = new RogueActuator(rogue, recorder)

      When("the user starts the game in transparent mode")
      player.start()
      
      And("the PC is killed")
      player.sendCommand(Command.REST)

      Then("the game should be over")
      recorder.gameOver should be(true)
      And("the final score should be shown")
      recorder.getScore should be(7)
    }

    Scenario("Rogue displays a -more- message") {
      Given("a game of Rogue in progress")
      val rogue: MockRogue = MoreGame.moreGame
      val recorder: Recorder = new Recorder
      val player: RogueActuator = new RogueActuator(rogue, recorder) // TODO Duplication

      When("the user enters a command to which Rogue responds with -more-")
      player.sendCommand(Command.RIGHT)

      Then("the final screen should be displayed")
      recorder.getScreen should be(MoreGame.thirdScreen)
    }
  }
}
