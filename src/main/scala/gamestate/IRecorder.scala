package gamestate

import expert.pGameState
import gamedata.{Fact, pInventory}

/** Interface for recorder that keeps track of current [[pGameState]] */
trait IRecorder {
  def getInventory: pInventory

  def getScreen: String

  def getScore: Int

  def gameOver: Boolean

  def knowledge: Set[Fact]
}
