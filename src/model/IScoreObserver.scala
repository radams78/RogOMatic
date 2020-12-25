package model

/** Interface for being notified of the score in the game over message */
trait IScoreObserver {
  def notifyScore(score: Int) : Unit
}
