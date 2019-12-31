package unit

import gamedata._
import mock.EitherAssertion._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Command

class GameStateTest extends AnyFlatSpec with Matchers {
  "Quaffing a potion of healing" should "record the power of healing" in {
    getEither(for {
      gs <- GameState.build(Command.Quaff(Some(Slot.A), Potion(1, Colour.RED)))
      gs2 <- Event.interpretMessage("you begin to feel better")
      gs3 <- gs.merge(gs2)
    } yield gs3).potionKnowledge.getPower(Colour.RED) should contain(PotionPower.HEALING)
  }
}
