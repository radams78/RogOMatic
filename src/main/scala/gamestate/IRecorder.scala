package gamestate

import expert.pGameState

/** Interface for recorder that keeps track of current [[pGameState]] */
trait IRecorder {
  /** The current state of the game */
  def gameState: pGameState

  /** The final score after the game has ended */
  def getScore: Int

  /** True if the game has ended */
  def gameOver: Boolean
}
