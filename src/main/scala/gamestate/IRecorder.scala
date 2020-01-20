package gamestate

import expert.pGameState
import gamedata.pInventory
import rogue.Command
import rogue.Event.Event

/** Input Interface for recorder that keeps track of current [[pGameState]] */
trait IInputRecorder {
  def recordEvent(e: Event): Either[String, Unit]

  def recordFinalScore(score: Int): Either[String, Unit]

  def recordScreen(screen: String): Either[String, Unit]

  def recordCommand(command: Command): Either[String, Unit]

  def recordInventory(inventory: pInventory): Unit
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
