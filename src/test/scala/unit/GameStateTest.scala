package unit

import gamedata._
import gamedata.items.{Colour, Potion, PotionPower}
import gamestate.GameState
import mock.EitherAssertion._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, Event}

class GameStateTest extends AnyFlatSpec with Matchers {
  "Quaffing a potion of healing" should "record the power of healing" in {
    getEither(for {
      e <- Event.interpretMessage("you begin to feel better")
      gs <- GameState(Command.Quaff(Slot.A, Potion(1, Colour.RED))).merge(e.inference)
    } yield gs).potionKnowledge.getPower(Colour.RED) should contain(PotionPower.HEALING)
  }
}
