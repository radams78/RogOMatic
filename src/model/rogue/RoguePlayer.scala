package model.rogue

import model.items.Inventory

/** Communicate with the game of Rogue */
class RoguePlayer private (rogue : IRogue) extends IRoguePlayer {
  private var inventoryObservers : Set[IInventoryObserver] = Set()
  private var screenObservers : Set[IScreenObserver] = Set()
  
  override def addInventoryObserver(observer: IInventoryObserver): Unit = 
    inventoryObservers = inventoryObservers + observer
  
  override def performCommand(command: Command): Unit = ()

  override def startGame(): Unit = {
    rogue.startGame()
    for (observer <- screenObservers) observer.notify(Screen.makeScreen(rogue.getScreenContents))
    for (observer <- inventoryObservers) observer.notify(Inventory())
  }

  override def addScoreObserver(observer: IScoreObserver): Unit = ()

  override def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  override def addScreenObserver(observer: IScreenObserver): Unit =
    screenObservers = screenObservers + observer
}

object RoguePlayer {
  def apply(rogue : IRogue) : IRoguePlayer = new RoguePlayer(rogue)
}