package unit

import gamedata.Domain._
import gamedata._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Command

class GameStateTest extends AnyFlatSpec with Matchers {
  "Quaffing a potion of healing" should "record the power of healing" in {
    new GameState(lastCommand = Some(Command.Quaff(Some(Slot.A), Potion(1, Colour.RED)))).merge(
      GameState.interpretMessage("you begin to feel better")) match {
      case Right(gs) => gs.potionKnowledge.getPower(Colour.RED) should contain(PotionPower.HEALING)
      case Left(s) => fail(s)
    }
  }
}
