package rogomatic

import domain.Domain._
import expert.{Expert, Transparent, pGameState}
import rogue._
import view.IView

import scala.annotation.tailrec

class RogOMatic(actuator: IRogueActuator, expert: Expert) {
  final def playRogue(view: IView): Unit = {
    @tailrec
    def playRogue0(history: History): Unit = history match {
      case gameOver: History.GameOver => view.displayGameOver(gameOver.score)
      case gameOn: History.GameOn =>
        (for {
          cmd <- expert.advice(gameOn)
          report <- actuator.sendCommand(cmd)
        } yield (cmd, report)) match {
          case Left(err) => view.displayError(err)
          case Right((cmd, report)) => playRogue0(gameOn.nextMove(cmd, report))
        }
    }

    actuator.start() match {
      case Left(err) => view.displayError(err)
      case Right(report) => playRogue0(History.FirstMove(report))
    }
  }
}

object RogOMatic {
  def transparent(rogue: IRogue, view: IView): RogOMatic = {
    val player: IRogueActuator = new RogueActuator(rogue)
    val expert: Expert = new Transparent(view)
    new RogOMatic(player, expert)
  }

}

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