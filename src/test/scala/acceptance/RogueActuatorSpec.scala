package acceptance

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
      val player: IRogueActuator = new RogueActuator(rogue)

      When("the user starts the game in transparent mode")
      player.start() match {
        case Right(_) => ()
        case Left(err) => fail(err)
      }

      And("the PC is killed")
      player.sendCommand(Command.REST) match {
        case Right(report: Report.GameOver) => report.score should be(7)
        case Right(report) => fail(s"Unexpected report: $report")
        case Left(err) => fail(err)
      }

      Then("the game should be over")
      And("the final score should be shown")
    }

    Scenario("Rogue displays a -more- message") {
      Given("a game of Rogue in progress")
      val rogue: MockRogue = MoreGame.moreGame
      val player: IRogueActuator = new RogueActuator(rogue)

      When("the user enters a command to which Rogue responds with -more-")
      player.sendCommand(Command.RIGHT) match {
        case Right(report: Report.GameOn) => report.screen should be(MoreGame.thirdScreen)
        case Right(report) => fail(s"Unexpected report: $report")
        case Left(err) => fail(err)
      }

      Then("the final screen should be displayed")
    }
  }
}
