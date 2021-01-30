import model.rogue.{IGameOverObserver, IRogue, IRoguePlayer, IScoreObserver, IScreenObserver, Rogue, RoguePlayer}
import view.{GameOverView, ScoreView, ScreenView}

object Main {
  def main(args : Array[String]): Unit = {
    val rogue : IRogue = Rogue()
    val player : IRoguePlayer = RoguePlayer(rogue)
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    player.addScreenObserver(screenView)
    player.addGameOverObserver(gameOverView)
    player.addScoreObserver(scoreView)
    
    player.startGame()
  }
}
