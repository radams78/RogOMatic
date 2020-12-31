package model.rogue

import model.{Command, IGameOverObserver, IInventoryObserver, IScoreObserver}

class RoguePlayer(rogue : IRogue) {
  def performCommand(command: Command): Unit = ()

  def startGame(): Unit = ()

  def addScoreObserver(observer: IScoreObserver): Unit = ()

  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  def addInventoryObserver(observer: IInventoryObserver): Unit = ()

  def addScreenObserver(observer: IScreenObserver): Unit = ()

}
