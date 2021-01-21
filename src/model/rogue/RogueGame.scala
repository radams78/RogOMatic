package model.rogue

/** A game of Rogue */
class RogueGame private (rogue : IRogue, player: RoguePlayer, screenReader: ScreenReader) {
  def addInventoryObserver(observer: IInventoryObserver): Unit = player.addInventoryObserver(observer)

  def performCommand(command: Command): Unit = player.performCommand(command)

  def addGameOverObserver(observer : IGameOverObserver) : Unit = player.addGameOverObserver(observer)
  def addScoreObserver(observer : IScoreObserver) : Unit = player.addScoreObserver(observer)
  def addScreenObserver(observer : IScreenObserver) : Unit = screenReader.addScreenObserver(observer) 
  
  def startGame(): Unit = new Thread(rogue).start()
}

object RogueGame {
  def apply(starter: Starter, buffer: Buffer, screenReader: ScreenReader): RogueGame = {
    val rogue : IRogue = new Rogue2(starter, buffer)
    val actuator : IActuator = Actuator(rogue)
    val screenReader: ScreenReader = ScreenReader()
    val player : RoguePlayer = RoguePlayer(actuator, screenReader)
    new RogueGame(rogue,player, screenReader)
  }
  
  def apply() : RogueGame = {
    val screenReader : ScreenReader = ScreenReader()
    val rogue : Rogue = new Rogue()
    val rogueProcess : RogueProcess = new RogueProcess(rogue.starter, rogue.buffer)
    val actuator : IActuator = Actuator(rogueProcess)
    val player : RoguePlayer = RoguePlayer(actuator, screenReader)
    new RogueGame(rogueProcess, player, screenReader)
  }
}
