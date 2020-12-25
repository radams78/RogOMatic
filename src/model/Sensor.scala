package model

import rogue.{IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor extends IScreenObserver {
  private var gameOverObservers: Seq[IGameOverObserver] = Seq()
  private var scoreObservers: Seq[IScoreObserver] = Seq()

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers :+= observer

  /** Add an observer that listens for the message about the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = scoreObservers :+= observer

  override def notify(screen: Screen): Unit = parseScreen(screen)

  private def parseScreen(screen: Screen): Unit = {

    if (isGameOverSreen(screen))
      parseGameOverScreen(screen)
    else
      parseNormalScreen(screen)

  }

  private def isGameOverSreen(screen: Screen) = {
    screen.lastLine.forall(_ == ' ')
  }

  private def parseNormalScreen(screen: Screen): Unit = {
    for (score <- Sensor.scoreLine.findFirstMatchIn(screen.firstLine)) {
      notifyScore(score.group("score").toInt)
    }
  }

  private def parseGameOverScreen(screen: Screen): Unit = {
    notifyGameOver()
  }

  private def notifyGameOver(): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver()
  }

  private def notifyScore(score: Int): Unit = {
    for (observer <- scoreObservers)
      observer.notifyScore(score)
  }

}

object Sensor {
  private val scoreLine: Regex = """with (?<score>\d+) gold""".r
}

class BadScreenFormatException(message: String) extends Exception(message)

class BadGameOverException(message: String) extends Exception(message)
