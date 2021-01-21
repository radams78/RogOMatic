import model.rogue.{IGameOverObserver, IScoreObserver, IScreenObserver, RoguePlayer}
import view.{GameOverView, ScoreView, ScreenView}

object Main {
  def main(args : Array[String]): Unit = {
    val player : RoguePlayer = RoguePlayer()
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    player.addScreenObserver(screenView)
    player.addGameOverObserver(gameOverView)
    player.addScoreObserver(scoreView)
    
    player.startGame()
  }
}
