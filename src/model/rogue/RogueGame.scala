package model.rogue

import model.{Command, IGameOverObserver, IScoreObserver, IScreenObserver}

class RogueGame private (rogue : IRogue, player: RoguePlayer, screenReader: ScreenReader) {
  def performCommand(command: Command): Unit = player.performCommand(command)

  def addGameOverObserver(observer : IGameOverObserver) : Unit = player.addGameOverObserver(observer)
  def addScoreObserver(observer : IScoreObserver) : Unit = player.addScoreObserver(observer)
  def addScreenObserver(observer : IScreenObserver) : Unit = screenReader.addScreenObserver(observer) 
  
  def startGame(): Unit = new Thread(rogue).start()
}

object RogueGame {
  def apply() : RogueGame = {
    var screenReader : ScreenReader = ScreenReader()
    val rogue : IRogue = Rogue(screenReader)
    val player : RoguePlayer = RoguePlayer(rogue, screenReader)

    new RogueGame(rogue, player, screenReader)
  }
}
