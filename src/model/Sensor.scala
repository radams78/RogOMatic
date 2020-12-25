package model

import rogue.{IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor extends IScreenObserver {
  private var gameOverObservers: Seq[IGameOverObserver] = Seq()
  private var scoreObservers: Seq[IScoreObserver] = Seq()

  private val scoreLine: Regex = """with (?<score>\d+) gold""".r

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers :+= observer

  def addScoreObserver(observer: IScoreObserver): Unit = scoreObservers :+= observer

  override def notify(screen: Screen): Unit = parseScreen(screen)

  private def parseScreen(screen: Screen): Unit = {

    if (isGameOverSreen(screen))
      parseGameOverScreen(screen)
    else
      parseNormalScreen(screen)

  }

  private def parseNormalScreen(screen: Screen): Unit = {
    for (score <- scoreLine.findFirstMatchIn(screen.firstLine)) {
      for (observer <- scoreObservers)
        observer.notify(score.group("score").toInt)
    }
  }

  private def parseGameOverScreen(screen: Screen): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver()
  }

  private def isGameOverSreen(screen: Screen) = {
    screen.lastLine.forall(_ == ' ')
  }
}

class BadScreenFormatException(message: String) extends Exception(message)

class BadGameOverException(message: String) extends Exception(message)
