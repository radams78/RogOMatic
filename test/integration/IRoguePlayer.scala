package integration

trait IRoguePlayer {
  def performCommand(command: Command): Unit

  def startGame(): Unit

  def addObserver(observer: IModelObserver): Unit

}
