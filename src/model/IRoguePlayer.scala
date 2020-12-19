package model

import gamedata.Command

/** Handle input and output with Rogue */
trait IRoguePlayer {
  /** Send the given command to Rogue */
  def performCommand(command: Command): Unit

  def startGame(): Unit

  def addScreenObserver(observer: IScreenObserver): Unit

  def addGameOverObserver(observer: IGameOverObserver): Unit
}
