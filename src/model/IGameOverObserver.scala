package model

/** An interface for notifying observers that the game is over */
trait IGameOverObserver {

  /** Notify the observer that the game is over
   *
   * @param score The final score */
  def notifyGameOver(score: Int): Unit
}
