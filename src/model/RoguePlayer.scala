package model

import model.items.Inventory
import model.rogue.{IRogue, Screen}

/** Communicate with the game of Rogue */
class RoguePlayer(rogue : IRogue) {
  private var screenObservers : Set[IScreenObserver] = Set()
  private var inventoryObservers : Set[IInventoryObserver] = Set()
  
  /** Send the given command to Rogue.
   * 
   * The game of Rogue must be in progress, otherwise throws a [[GameNotInProgressException]]
   * 
   * @param command Command to be performed */
  def performCommand(command: Command): Unit = ()

  /** Start the game of Rogue 
   * 
   * If the game of Rogue has already started, throws a [[GameStartedException]]. If the first screen cannot be
   * retrieved from Rogue, throws an [[EmptyScreenException]]. */
  def startGame(): Unit = {
    rogue.startGame()
    readAllFromRogue()
  }

  private def readAllFromRogue(): Unit = {
    readNormalScreen()
    readInventoryScreen()
  }

  private def readNormalScreen(): Unit = {
    readScreen()
  }

  private def readInventoryScreen(): Unit = {
    displayInventoryScreen()
    val inventoryScreen: Screen = readScreen()
    parseInventoryScreen(inventoryScreen)
    rogue.sendKeypress(' ')
  }

  private def parseInventoryScreen(inventoryScreen : Screen): Unit = {
    for (observer <- inventoryObservers) observer.notify(Inventory())
  }

  private def displayInventoryScreen(): Unit = {
    rogue.sendKeypress('i')
  }

  /** Add an observer that listens for the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = ()

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  /** Add an observer that listens for the current inventory */
  def addInventoryObserver(observer: IInventoryObserver): Unit = inventoryObservers = inventoryObservers + observer

  /** Add an observer that listens for the screen retrieved from Rogue */
  def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer

  private def readScreen(): Screen = {
    val screen: Screen = rogue.getScreen.getOrElse(throw new EmptyScreenException)
    notifyScreen(screen)
    screen
  }

  private def notifyScreen(screen: Screen): Unit = {
    for (observer <- screenObservers) observer.notify(screen)
  }
}

/** Exception thrown if the Rogue process has ended unexpectedly. */
class GameNotInProgressException extends Exception

/** Exception thrown if the Rogue process has started unexpectedly. */
class GameStartedException extends Exception

/** Exception thrown if screen cannot be retrieved from Rogue. */
class EmptyScreenException extends Exception