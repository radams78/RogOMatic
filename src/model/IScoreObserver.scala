package model

/** Interface for being notified of the score in the game over message */
trait IScoreObserver {
  def notify(score: Int) : Unit
}
