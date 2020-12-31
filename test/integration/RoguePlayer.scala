package integration

import model.{Command, IGameOverObserver, IInventoryObserver, IScoreObserver}
import model.rogue.{IRogue, IScreenObserver}

class RoguePlayer(rogue : IRogue) {
  def performCommand(command: Command): Unit = ()

  def startGame(): Unit = ()

  def addScoreObserver(observer: IScoreObserver): Unit = ()

  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  def addInventoryObserver(observer: IInventoryObserver): Unit = ()

  def addScreenObserver(observer: IScreenObserver): Unit = ()

}
