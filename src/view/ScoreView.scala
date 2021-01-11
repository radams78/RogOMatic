package view

import model.rogue.IScoreObserver

class ScoreView extends IScoreObserver {
  override def notifyScore(score: Int): Unit = System.out.println("Final score: " + score)
}
