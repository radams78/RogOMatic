package model

import model.items.{Arrows, Food, Inventory, Mace, RingMail, ShortBow, Slot}
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
    val inventory1 : Inventory = Inventory(
      Map(
        Slot.A -> Food,
        Slot.B -> RingMail(+1),
        Slot.C -> Mace(+1, +1),
        Slot.D -> ShortBow(+1, +0),
        Slot.E -> Arrows(32, +0, +0)
      ),
      wearing = Slot.B,
      wielding = Slot.C
    )

    rogue.startGame()
    readScreen()
    for (observer <- inventoryObservers) observer.notify(inventory1)
  }

  /** Add an observer that listens for the final score */
  def addScoreObserver(observer: IScoreObserver): Unit = ()

  /** Add an observer that listens for the message that the game is over */
  def addGameOverObserver(observer: IGameOverObserver): Unit = ()

  /** Add an observer that listens for the current inventory */
  def addInventoryObserver(observer: IInventoryObserver): Unit = inventoryObservers = inventoryObservers + observer

  /** Add an observer that listens for the screen retrieved from Rogue */
  def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer

  private def readScreen(): Unit = {
    val screen: Screen = rogue.getScreen.getOrElse(throw new EmptyScreenException)
    notifyScreen(screen)
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