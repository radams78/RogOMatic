package model

import model.rogue.IRogue

class RoguePlayer(rogue : IRogue) {
  def performCommand(command: Command): Unit = ()

  def startGame(): Unit = rogue.startGame()

  def addScoreObserver(observer: IScoreObserver): Unit = ()

  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  def addInventoryObserver(observer: IInventoryObserver): Unit = ()

  def addScreenObserver(observer: IScreenObserver): Unit = ()

}
