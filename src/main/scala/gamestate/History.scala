package gamestate

import domain.Domain._
import gamedata.ProvidesKnowledge._
import gamedata.UsesKnowledge._
import gamedata._

/** The history of the game, holding all commands sent to Rogue and all reports retrieved from Rogue */
trait History

object History {

  /** The history of a game that is not yet finished */
  trait GameOn extends History {
    def inventory: Either[String, pInventory]

    def screen: String

    /** Add a move to the history */
    def nextMove(cmd: pCommand, report: Report): History = report match {
      case report: Report.GameOn => NextMove(this, cmd, report)
      case report: Report.GameOver => GameOver(this, cmd, report)
    }

    /** The current stae of the game that can be inferred from the history */
    def gameState: Either[String, pGameState]
  }

  object GameOn {
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self.gameState match {
      case Left(_) => Set()
      case Right(gs) => gs.implications
    }
  }

  /** The history of a game before the first move is made */
  case class FirstMove(report: Report.GameOn) extends GameOn {
    override def gameState: Either[String, pGameState] =
      for {
        gs <- report.inferences
        gs2 <- pGameState().merge(gs)
      } yield gs2

    override def screen: String = report.screen

    override def inventory: Either[String, pInventory] = Right(report.inventory)
  }

  /** The history of a game with one or more moves that is not yet finished */
  case class NextMove(history: GameOn, command: pCommand, report: Report.GameOn) extends GameOn {
    override def gameState: Either[String, pGameState] = for {
      gs <- history.gameState
      gs2 <- gs.merge(pGameState(command))
      gs3 <- report.inferences
      gs4 <- gs2.merge(gs3)
    } yield gs4

    override def screen: String = report.screen

    override def inventory: Either[String, pInventory] = for {
      i1 <- history.inventory
      i2 <- i1.infer(command)
      i3 <- i2.merge(report.inventory)
      i4 <- i3.infer(report) // TODO Ugly?
    } yield i4
  }

  /** The complete history of a finished game of Rogue */
  case class GameOver(history: GameOn, command: pCommand, report: Report.GameOver) extends History {
    /** Final score */
    def score: Int = report.score

  }

}