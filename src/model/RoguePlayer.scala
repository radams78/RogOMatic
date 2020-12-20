package model

import gamedata.Command
import rogue.IRogue

/** Handle input and output with Rogue.
 *
 * @param rogue The Rogue process */
// TODO Split into two?


class RoguePlayer(rogue: IRogue) extends IRoguePlayer with IScreenObserver {
  rogue.addScreenObserver(this)

  private var screen: Option[Seq[String]] = None
  private var gameOverObservers : Set[IGameOverObserver] = Set()

  override def performCommand(command: Command): Unit = {
    for (keypress <- command.keypresses) rogue.sendKeypress(keypress)
    readScreen()
  }

  override def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers += observer

  private def readScreen(): Unit = {
    notifyGameOverObserversIfNecessary()
  }

  private def notifyGameOverObserversIfNecessary(): Unit = {
    if (isStatusLineBlank)
      notifyGameOverObservers()
  }

  private def notifyGameOverObservers(): Unit = {
    for (observer <- gameOverObservers)
      observer.notifyGameOver(0)
  }

  private def isStatusLineBlank: Boolean = {
    screen.exists(_.last.forall(_ == ' '))
  }

  /** Notify all observers that this is the screen displayed by Rogue */
  override def notify(_screen: Seq[String]): Unit = screen = Some(_screen)
}