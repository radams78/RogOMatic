package model

import rogue.IScreenObserver

class Sensor extends IScreenObserver {
  private var observers: Seq[IGameOverObserver] = Seq()

  def addGameOverObserver(observer: IGameOverObserver): Unit = observers :+= observer

  /** Notify all observers that this is the screen displayed by Rogue */
  override def notify(screen: Seq[String]): Unit = for (observer <- observers) observer.notifyGameOver(10)
}
