package model

import rogue.IScreenObserver

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor extends IScreenObserver {
  private var observers: Seq[IGameOverObserver] = Seq()
  private var _score : Option[Int] = None

  private val scoreLine: Regex = """with (?<score>\d+) gold""".r

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = observers :+= observer

  override def notify(screen: Seq[String]): Unit = {
    for (score <- scoreLine.findFirstMatchIn(
      screen.headOption
        .getOrElse(throw new BadScreenFormatException("Notified of empty screen"))
    )) {
      _score = Some(score.group("score").toInt)
    }
    if (screen.lastOption
    .getOrElse(throw new BadScreenFormatException("Notified of empty screen"))
    .forall(_ == ' '))
      for (observer <- observers)
        observer.notifyGameOver(_score.getOrElse(throw new BadGameOverException("Game over screen received without final score")))
  }
}

class BadScreenFormatException(message: String) extends Exception(message)

class BadGameOverException(message: String) extends Exception(message)
