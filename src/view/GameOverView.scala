package view

import model.IGameOverObserver

class GameOverView extends IGameOverObserver {
  override def notifyGameOver(): Unit = println("==== GAME OVER ====")
}
