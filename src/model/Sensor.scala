package model

import gamedata._
import rogue.{IRogue, IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor(rogue: IRogue) extends IScreenObserver {
  rogue.addScreenObserver(this)

  // TODO Make sets
  private var gameOverObservers: Set[IGameOverObserver] = Set()
  private var inventoryObservers: Set[IInventoryObserver] = Set()
  private var scoreObservers: Set[IScoreObserver] = Set()
  private var state: State = AfterCommand

  def addInventoryObserver(observer: IInventoryObserver): Unit = inventoryObservers = inventoryObservers + observer

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers = gameOverObservers + observer

  /** Add an observer that listens for the message about the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = scoreObservers  = scoreObservers + observer

  override def notify(screen: Screen): Unit = {
    state = state.parseScreen(screen)
    state.sendKeypressesToRogue()
  }
  
  private def notifyScore(score: Int): Unit = {
    for (observer <- scoreObservers)
      observer.notifyScore(score)
  }

  private def parseGameOverScreen(screen: Screen): Unit = {
    notifyGameOver()
  }

  private def notifyGameOver(): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver()
  }

  trait State {
    def sendKeypressesToRogue(): Unit = ()

    def parseScreen(screen: Screen): State

  }

  object AfterCommand extends State {
    override def parseScreen(screen: Screen): State = {

      if (isGameOverScreen(screen)) {
        parseGameOverScreen(screen)
        GameOver
      } else {
        parseNormalScreen(screen)
        Inventory
      }

    }

    private def isGameOverScreen(screen: Screen): Boolean = {
      screen.lastLine.forall(_ == ' ')
    }

    private def parseNormalScreen(screen: Screen): Unit = {
      for (score <- Sensor.scoreLine.findFirstMatchIn(screen.firstLine)) {
        notifyScore(score.group("score").toInt)
      }
    }
  }

  object GameOver extends State {
    override def parseScreen(screen: Screen): State = this
  }

  object Inventory extends State {

    override def parseScreen(screen: Screen): State = {
      parseInventoryScreen(screen)
      BeforeCommand
    }

    override def sendKeypressesToRogue(): Unit = rogue.sendKeypress('i')
    
    private def parseInventoryScreen(screen: Screen): Unit =
      for (observer <- inventoryObservers)
        observer.notify(gamedata.Inventory(
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

  object BeforeCommand extends State {

    def parseNormalScreen(screen: Screen): Unit = ()

    override def parseScreen(screen: Screen): State = {
      parseNormalScreen(screen)
      AfterCommand
    }

    override def sendKeypressesToRogue(): Unit = rogue.sendKeypress(' ')
  }

}

object Sensor {
  // Regex for parsing message about final score
  private val scoreLine: Regex = """with (?<score>\d+) gold""".r
}
