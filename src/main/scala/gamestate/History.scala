package gamestate

import domain.Domain._
import expert.pGameState
import rogue.{Command, Report}

trait History

object History {

  trait GameOn extends History {
    def nextMove(cmd: Command, report: Report): History = report match {
      case report: Report.GameOn => NextMove(this, cmd, report)
      case report: Report.GameOver => GameOver(this, cmd, report)
    }

    def gameState: Either[String, pGameState]
  }

  case class FirstMove(report: Report.GameOn) extends GameOn {
    override def gameState: Either[String, pGameState] =
      for {
        gs <- report.inferences
        gs2 <- pGameState().merge(gs)
      } yield gs2
  }

  case class NextMove(history: GameOn, command: Command, report: Report.GameOn) extends GameOn {
    override def gameState: Either[String, pGameState] = for {
      gs <- history.gameState
      gs2 <- gs.merge(pGameState(command))
      gs3 <- report.inferences
      gs4 <- gs2.merge(gs3)
    } yield gs4
  }

  case class GameOver(history: GameOn, command: Command, report: Report.GameOver) extends History {
    def score: Int = report.score

  }

}