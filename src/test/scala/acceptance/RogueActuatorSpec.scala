package acceptance

import domain.pLift
import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Acceptance tests for playing Rogue in transparent mode */
class RogueActuatorSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  Feature("Play a game of Rogue in transparent mode") {
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
      recorder.gameState.screen should be(pLift.Known(MoreGame.thirdScreen))
    }
  }
}
