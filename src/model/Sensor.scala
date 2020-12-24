package model

import rogue.{IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor extends IScreenObserver {
  private var gameOverObservers: Seq[IGameOverObserver] = Seq()
  private var _score : Option[Int] = None

  private val scoreLine: Regex = """with (?<score>\d+) gold""".r

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers :+= observer

  override def notify(screen: Screen): Unit = parseScreen(screen)

  private def parseScreen(screen: Screen): Unit = {

    if (isGameOverSreen(screen))
      parseGameOverScreen(screen)
    else
      parseNormalScreen(screen)

  }

  private def parseNormalScreen(screen: Screen): Unit = {
    for (score <- scoreLine.findFirstMatchIn(screen.firstLine)) {
      _score = Some(score.group("score").toInt)
    }
  }

  private def parseGameOverScreen(screen: Screen): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver(_score.getOrElse(throw new BadGameOverException("Game over screen received without final score")))
  }

  private def isGameOverSreen(screen: Screen) = {
    screen.lastLine.forall(_ == ' ')
  }
}

class BadScreenFormatException(message: String) extends Exception(message)

class BadGameOverException(message: String) extends Exception(message)
