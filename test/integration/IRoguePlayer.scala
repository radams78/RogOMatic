package integration

/** Handle input and output with Rogue */
trait IRoguePlayer {
  def performCommand(command: Command): Unit

  def startGame(): Unit

  def addObserver(observer: IModelObserver): Unit

}
