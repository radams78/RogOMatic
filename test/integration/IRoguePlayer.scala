package integration

import gamedata.Command

/** Handle input and output with Rogue */
trait IRoguePlayer {
  def performCommand(command: Command): Unit

  def startGame(): Unit

  def addObserver(observer: IRoguePlayerObserver): Unit

}
