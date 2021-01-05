package model

import model.rogue.{IRogue, Screen}

/** Communicate with the game of Rogue */
class RoguePlayer(rogue : IRogue) {
  private var screenObservers : Set[IScreenObserver] = Set()
  
  /** Send the given command to Rogue.
   * 
   * The game of Rogue must be in progress, otherwise throws an [[GameNotInProgressException]]
   * 
   * @param command Command to be performed */
  def performCommand(command: Command): Unit = ()

  /** Start the game of Rogue 
   * 
   * If the game of Rogue has already started, throws an [[GameStartedException]]*/
  def startGame(): Unit = {
    rogue.startGame()
    val screen : Screen = rogue.getScreen.getOrElse(throw new EmptyScreenException)
    notifyScreen(screen)
  }

  private def notifyScreen(screen: Screen): Unit = {
    for (observer <- screenObservers) observer.notify(screen)
  }

  /** Add an observer that listens for the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = ()

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  /** Add an observer that listens for the current inventory */
  def addInventoryObserver(observer: IInventoryObserver): Unit = ()

  /** Add an observer that listens for the screen retrieved from Rogue */
  def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer

}

class GameNotInProgressException extends Exception

class GameStartedException extends Exception

class EmptyScreenException extends Exception