package model

import model.items.{IInventoryParser, Inventory}
import model.rogue.{IRogue, IScreenObserver, Screen}

import scala.util.matching.Regex

/** Parse the information from the screen of Rogue and notify the relevant observers */
class Sensor(rogue: IRogue, inventoryParser : IInventoryParser) extends IScreenObserver {
  rogue.addScreenObserver(this)

  private var state: State = Ready

  def addInventoryObserver(observer: IInventoryObserver): Unit = Inventory.addObserver(observer)
    
  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = GameOver.addGameOverObserver(observer)
  
  /** Add an observer that listens for the message about the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = Ready.addObserver(observer)

  override def notify(screen: Screen): Unit = {
    state = state.parseScreen(screen)
    state.sendKeypressesToRogue()
  }
  
  trait State {
    def sendKeypressesToRogue(): Unit = ()

    def parseScreen(screen: Screen): State

  }

  object Ready extends State {
    def addObserver(observer: IScoreObserver): Unit = scoreObservers = scoreObservers + observer

    private var scoreObservers: Set[IScoreObserver] = Set()

    override def parseScreen(screen: Screen): State = {

      if (isGameOverScreen(screen)) {
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

    private def notifyScore(score: Int): Unit = {
      for (observer <- scoreObservers)
        observer.notifyScore(score)
    }
  }

  object GameOver extends State {
    private var gameOverObservers: Set[IGameOverObserver] = Set()

    def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers = gameOverObservers + observer

    override def sendKeypressesToRogue(): Unit = notifyGameOver()

    override def parseScreen(screen: Screen): State = this

    private def notifyGameOver(): Unit = {
      for (observer <- gameOverObservers)
        observer.notifyGameOver()
    }
  }

  object Inventory extends State {
    def addObserver(observer: IInventoryObserver): Unit = inventoryObservers = inventoryObservers + observer

    private var inventoryObservers: Set[IInventoryObserver] = Set()

    override def parseScreen(screen: Screen): State = {
      parseInventoryScreen(screen)
      CancelInventory
    }

    override def sendKeypressesToRogue(): Unit = rogue.sendKeypress('i')
    
    private def parseInventoryScreen(screen: Screen): Unit = { 
      val inventory : Inventory = inventoryParser.parseInventoryScreen(screen)
      for (observer <- inventoryObservers)
        observer.notify(inventory)
    }
  }

  object CancelInventory extends State {
    
    override def parseScreen(screen: Screen): State = Ready

    override def sendKeypressesToRogue(): Unit = rogue.sendKeypress(' ')
  }

}

object Sensor {
  // Regex for parsing message about final score
  private val scoreLine: Regex = """with (?<score>\d+) gold""".r
}
