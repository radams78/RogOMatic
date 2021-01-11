import controller.TransparentController
import model.rogue.{IGameOverObserver, IScoreObserver, IScreenObserver, RogueGame}
import view.{GameOverView, ScoreView, ScreenView}

object Main {
  def main(args : Array[String]): Unit = {
    val rogueGame : RogueGame = RogueGame()
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    rogueGame.addScreenObserver(screenView)
    rogueGame.addGameOverObserver(gameOverView)
    rogueGame.addScoreObserver(scoreView)
    
    rogueGame.startGame()
    new Thread(new TransparentController(rogueGame)).start()
  }
}
