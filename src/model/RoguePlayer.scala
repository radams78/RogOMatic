package model

import gamedata.Command
import rogue.IRogue

/** Handle input and output with Rogue.
 *
 * @param rogue The Rogue process */
// TODO Split into two?


class RoguePlayer(rogue: IRogue) extends IRoguePlayer {

  private var screenObservers : Set[IScreenObserver] = Set()
  private var gameOverObservers : Set[IGameOverObserver] = Set()

  override def startGame(): Unit =
    notifyScreen()

  override def performCommand(command: Command): Unit = {
    for (keypress <- command.keypresses) rogue.sendKeypress(keypress)
    readScreen()
  }

  override def addScreenObserver(observer: IScreenObserver): Unit = screenObservers += observer

  override def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers += observer

  private def readScreen(): Unit = {
    notifyScreen()
    notifyGameOverObserversIfNecessary()
  }

  private def notifyScreen(): Unit = {
    for (observer <- screenObservers) observer.notify(rogue.readScreen)
  }

  private def notifyGameOverObserversIfNecessary(): Unit = {
    if (isStatusLineBlank)
      notifyGameOverObservers()
  }

  private def notifyGameOverObservers(): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver(0)
  }

  private def isStatusLineBlank = {
    rogue.readScreen.last.forall(_ == ' ')
  }
}