package integration

import rogue.IRogue

class RoguePlayer(rogue: IRogue) extends IRoguePlayer {
  private var observers : Set[IModelObserver] = Set()
  
  override def startGame(): Unit =
    for (observer <- observers) observer.notify(rogue.readScreen)

  override def addObserver(observer: IModelObserver): Unit =
    observers = observers + observer

  override def performCommand(command: Command): Unit = ()
}