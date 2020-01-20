package gamestate

import expert.pGameState
import gamedata.pInventory
import rogue.Command
import rogue.Event.Event

/** Interface for recorder that keeps track of current [[pGameState]] */
trait IRecorder {
  def recordEvent(e: Event): Either[String, Unit]

  def recordFinalScore(score: Int): Either[String, Unit]

  def recordScreen(screen: String): Either[String, Unit]

  def recordCommand(command: Command): Either[String, Unit]

  def recordInventory(inventory: pInventory): Unit

  /** The current state of the game */
  def gameState: pGameState

  /** The final score after the game has ended */
  def getScore: Int

  /** True if the game has ended */
  def gameOver: Boolean
}
