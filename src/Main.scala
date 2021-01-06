import controller.TransparentController
import model.rogue.Rogue
import model.{IGameOverObserver, IScoreObserver, IScreenObserver, RoguePlayer, ScreenReader}
import view.{GameOverView, ScoreView, ScreenView}

object Main {
  def main(args : Array[String]): Unit = {
    var screenReader : ScreenReader = new ScreenReader
    val rogue : Rogue = new Rogue(screenReader)
    val player : RoguePlayer = new RoguePlayer(rogue, screenReader)
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    screenReader.addScreenObserver(screenView)
    player.addGameOverObserver(gameOverView)
    player.addScoreObserver(scoreView)
    
    new Thread(rogue).start()
    new Thread(new TransparentController(player)).start()
  }
}
