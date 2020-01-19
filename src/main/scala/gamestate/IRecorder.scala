package gamestate

import expert.pGameState

/** Interface for recorder that keeps track of current [[pGameState]] */
trait IRecorder {
  def gameState: pGameState

  def getScore: Int

  def gameOver: Boolean
}
