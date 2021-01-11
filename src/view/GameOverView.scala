package view

import model.rogue.IGameOverObserver

class GameOverView extends IGameOverObserver {
  override def notifyGameOver(): Unit = println("==== GAME OVER ====")
}
