package gamestate

import expert.pGameState
import gamedata.pInventory

/** Interface for recorder that keeps track of current [[pGameState]] */
trait IRecorder {
  def getInventory: pInventory

  def getScore: Int

  def gameOver: Boolean
}
