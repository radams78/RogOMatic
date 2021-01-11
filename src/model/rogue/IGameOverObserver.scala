package model.rogue

/** An interface for notifying observers that the game is over */
trait IGameOverObserver {

  /** Notify the observer that the game is over */
  def notifyGameOver(): Unit
}
