package gamestate

import expert.pGameState
import gamedata.pInventory
import rogue.Command
import rogue.Event.Event

/** Input Interface for recorder that keeps track of current [[pGameState]] */
trait IInputRecorder {
  /** Record that an event has occurred */
  def recordEvent(e: Event): Either[String, Unit]

  /** Record final score after game is over */
  def recordFinalScore(score: Int): Either[String, Unit]

  /** Record current screen displayed by Rogue */
  def recordScreen(screen: String): Either[String, Unit]

  /** Record that command has just been performed */
  def recordCommand(command: Command): Either[String, Unit]

  /** Record current inventory displayed by Rogue */
  def recordInventory(inventory: pInventory): Either[String, Unit]
}

/** Output interface for recorder that keeps track of current [[pGameState]] */
trait IOutputRecorder {
  /** The current state of the game */
  def gameState: pGameState

  /** The final score after the game has ended */
  def getScore: Int

  /** True if the game has ended */
  def gameOver: Boolean
}
