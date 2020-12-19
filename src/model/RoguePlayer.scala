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

  private def notifyScreen(): Unit = {
    for (observer <- screenObservers) observer.notify(rogue.readScreen)
  }

  override def addObserver(observer: IScreenObserver): Unit = screenObservers += observer

  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers += observer

  override def performCommand(command: Command): Unit = {
    for (keypress <- command.keypresses) rogue.sendKeypress(keypress)
    readScreen()
  }

  private def readScreen(): Unit = {
    notifyScreen()
    if (rogue.readScreen.last == " " * 80)
      for (observer <- gameOverObservers)
        observer.notifyGameOver(0)
  }
}
