package gamedata

import rogue.Command

import scala.util.matching.Regex

/** Partial information about the state of the game */
class GameState(val scrollPowers: Map[String, ScrollPower]) {
  def interpretMessage(messageLine: String, lastCommand: Command): Either[String, GameState] = messageLine match {
    case GameState.removeCurseMessage() => lastCommand match {
      case Command.Read(slot, scroll) =>
        Right(new GameState(scrollPowers.updated(scroll.title, ScrollPower.REMOVE_CURSE)))
      case cmd => Left("Received remove curse message but did not read scroll")
    }
    case _ => Right(this)
  }
}

object GameState {
  private val removeCurseMessage: Regex = """you feel as though someone is watching over you""".r.unanchored
}