package model.rogue

trait IRoguePlayer {
  def addInventoryObserver(observer: IInventoryObserver)  : Unit

  def performCommand(command: Command): Unit

  def startGame(): Unit

  def addScoreObserver(observer: IScoreObserver)

  def addGameOverObserver(observer: IGameOverObserver)

  def addScreenObserver(observer: IScreenObserver)

}
