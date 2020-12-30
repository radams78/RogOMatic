import model.{IGameOverObserver, IScoreObserver, Sensor}
import rogue.IScreenObserver

object Main {
  def main(args : Array[String]): Unit = {
    val rogue : Rogue = new Rogue
    val sensor : Sensor = new Sensor(rogue)
    val screenView : IScreenObserver = new ScreenView
    val gameOverView : IGameOverObserver = new GameOverView
    val scoreView : IScoreObserver = new ScoreView
    
    rogue.addScreenObserver(sensor)
    rogue.addScreenObserver(screenView)
    sensor.addGameOverObserver(gameOverView)
    sensor.addScoreObserver(scoreView)
    
    new Thread(rogue).start()
    new Thread(new TransparentController(rogue)).start()
  }
}
