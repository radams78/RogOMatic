package rogue

import gamedata.pInventory
import rogue.Event.Event

trait Report {
  def screen: String
}

object Report {

  case class GameOn(override val screen: String, inventory: pInventory, events: Set[Event]) extends Report

  case class GameOver(override val screen: String, score: Int) extends Report

}