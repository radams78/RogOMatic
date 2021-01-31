package model.rogue

/** A facade for the [[model.rogue]] package */
trait IRoguePlayer {
  /** Start the game of Rogue */
  def startGame(): Unit

  def performCommand(command: Command): Unit

  def addScreenObserver(observer: IScreenObserver)

  def addInventoryObserver(observer: IInventoryObserver)  : Unit

  def addScoreObserver(observer: IScoreObserver)

  def addGameOverObserver(observer: IGameOverObserver)
}
