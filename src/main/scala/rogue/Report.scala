package rogue

import gamedata.pInventory
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
  case class GameOn(override val screen: String, inventory: pInventory, events: Set[Event]) extends Report

  case class GameOver(override val screen: String, score: Int) extends Report

}