package model

import gamedata.{Arrows, Food, Mace, RingMail, ShortBow, Slot}
import rogue.{IRogue, IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor(rogue: IRogue) extends IScreenObserver {
  rogue.addScreenObserver(this)
  def addInventoryObserver(observer: IInventoryObserver): Unit = inventoryObservers :+= observer 

  // TODO Make sets
  private var gameOverObservers: Seq[IGameOverObserver] = Seq()
  private var inventoryObservers: Seq[IInventoryObserver] = Seq()
  private var scoreObservers: Seq[IScoreObserver] = Seq()

  private var state : State = AfterCommand
  
  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers :+= observer

  /** Add an observer that listens for the message about the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = scoreObservers :+= observer

  override def notify(screen: Screen): Unit = parseScreen(screen)

  private def parseScreen(screen: Screen): Unit = {

    if (isGameOverScreen(screen))
      parseGameOverScreen(screen)
    else
      parseNormalScreen(screen)

  }

  private def isGameOverScreen(screen: Screen): Boolean = {
    screen.lastLine.forall(_ == ' ')
  }

  private def parseNormalScreen(screen: Screen): Unit = {
    for (score <- Sensor.scoreLine.findFirstMatchIn(screen.firstLine)) {
      notifyScore(score.group("score").toInt)
    }
    for (observer <- inventoryObservers)     observer.notify(gamedata.Inventory(
      Map(
        Slot.A -> Food,
        Slot.B -> RingMail(+1),
        Slot.C -> Mace(+1, +1),
        Slot.D -> ShortBow(+1, +0),
        Slot.E -> Arrows(32, +0, +0)
      ),
      wearing = Slot.B,
      wielding = Slot.C
    ))
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

  trait State

  object AfterCommand extends State

}

object Sensor {
  // Regex for parsing message about final score
  private val scoreLine: Regex = """with (?<score>\d+) gold""".r
}
