package gamedata

import gamedata.fact.ProvidesKnowledge
import gamedata.fact.ProvidesKnowledge._
import gamestate.Inventory
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
  case class GameOn(override val screen: String, inventory: Inventory, events: Set[Event], lastCommand: pCommand) extends Report {
  }

  object GameOn {
    def build(screen: String, inventory: Inventory, events: Set[Event]): Either[String, GameOn] = {
      for (inferences <- {
        events.foldLeft[Either[String, pCommand]](Right(pCommand.UNKNOWN))({ case (x, event) => x match {
          case Right(cmd) => cmd.merge(event.inference)
          case Left(err) => Left(err)
        }
        })
      }) yield GameOn(screen, inventory, events, inferences)
    }
    
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self.inventory.implications.union(
      self.events.flatMap((e: Event) => e.implications)
    )
  }

  case class GameOver(override val screen: String, score: Int) extends Report

}