import model.rogue.{IScreenObserver, RoguePlayer}
import model.{IGameOverObserver, IScoreObserver}

object Main {
  def main(args : Array[String]): Unit = {
    val rogue : Rogue = new Rogue
    val player : RoguePlayer = new RoguePlayer(rogue)
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    player.addScreenObserver(screenView)
    player.addGameOverObserver(gameOverView)
    player.addScoreObserver(scoreView)
    
    new Thread(rogue).start()
    new Thread(new TransparentController(player)).start()
  }
}
