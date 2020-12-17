package model

trait IGameOverObserver {

  def notifyGameOver(score: Int): Unit
}
