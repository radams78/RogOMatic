package gamedata

import domain.Domain._
import domain.pLift
import gamedata.ProvidesKnowledge._
import gamestate.pGameState
import rogue.Event.Event

/** Report returned from parsing screens from Rogue */
trait Report {
  /** Final screen displayed by Rogue */
  def screen: String
}

object Report {

  /** Game is still in progress
   *
   * @param events Set of events reported by message lines */
  case class GameOn(override val screen: String, inventory: pInventory, events: Set[Event]) extends Report {
    def inferences: Either[String, pGameState] = {
      val gs0: pGameState = pGameState(pLift.Known(screen), inventory, Set(), pLift.UNKNOWN)
      events.foldLeft[Either[String, pGameState]](Right(gs0))({ case (x, event) => x match {
        case Right(gs) => gs.merge(event.inference)
        case Left(err) => Left(err)
      }
      })
    }
  }

  object GameOn {
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self.inventory.implications.union(
      self.events.flatMap((e: Event) => e.implications)
    )
  }

  case class GameOver(override val screen: String, score: Int) extends Report

}