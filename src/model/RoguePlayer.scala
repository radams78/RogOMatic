package model

import gamedata.Command
import rogue.IRogue

/** Handle input and output with Rogue.
 *
 * @param rogue The Rogue process */
// TODO Split into two?


class RoguePlayer(rogue: IRogue) extends IRoguePlayer {
  def addGameOverObserver(observer: IGameOverObserver): Unit = gameOverObservers += observer

  private var observers : Set[IRoguePlayerObserver] = Set()
  private var gameOverObservers : Set[IGameOverObserver] = Set()

  override def startGame(): Unit =
    for (observer <- observers) observer.notify(rogue.readScreen)

  override def addObserver(observer: IRoguePlayerObserver): Unit =
    observers = observers + observer

  override def performCommand(command: Command): Unit = {
    for (keypress <- command.keypresses) rogue.sendKeypress(keypress)
    if (rogue.readScreen.last == " " * 80)
      for (observer <- gameOverObservers)
        observer.notifyGameOver(0)
    else
      for (observer <- observers)
        observer.notify(rogue.readScreen)
  }
}
