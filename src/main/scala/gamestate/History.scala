package gamestate

import domain.Domain._
import domain.pLift
import gamedata.ProvidesKnowledge._
import gamedata.UsesKnowledge._
import gamedata._

/** The history of the game, holding all commands sent to Rogue and all reports retrieved from Rogue */
trait History

object History {

  /** The history of a game that is not yet finished */
  trait GameOn extends History {
    def inventory: pInventory

    def screen: String

    /** Add a move to the history */
    def nextMove(cmd: pCommand, report: Report): Either[String, History] = report match {
      case report: Report.GameOn => NextMove.build(this, cmd, report)
      case report: Report.GameOver => Right(GameOver(this, cmd, report))
    }

    /** The current stae of the game that can be inferred from the history */
    def gameState: pGameState

    def knowledge: Set[Fact] = gameState.knowledge

    def lastCommand: pLift[Option[pCommand]] = gameState.lastCommand
  }

  object GameOn {
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self.inventory.implications union
      self.knowledge union
      (self.lastCommand match {
        case pLift.UNKNOWN => Set()
        case pLift.Known(None) => Set()
        case pLift.Known(Some(command)) => command.implications
      })
  }

  /** The history of a game before the first move is made */
  case class FirstMove(report: Report.GameOn) extends GameOn {
    override def gameState: pGameState = report.inferences

    override def screen: String = report.screen

    override def inventory: pInventory = report.inventory
  }

  /** The history of a game with one or more moves that is not yet finished */
  case class NextMove(history: GameOn, command: pCommand, report: Report.GameOn, override val gameState: pGameState, override val inventory: pInventory) extends GameOn {
    override def screen: String = report.screen
  }

  object NextMove {
    def build(history: GameOn, command: pCommand, report: Report.GameOn): Either[String, NextMove] = for {
      gs2 <- history.gameState.merge(pGameState(command))
      gs4 <- gs2.merge(report.inferences)
      i2 <- history.inventory.infer(command)
      i3 <- i2.merge(report.inventory)
      i4 <- i3.infer(report) // TODO Ugly?
    } yield NextMove(history, command, report, gs4, i4)

  }

  /** The complete history of a finished game of Rogue */
  case class GameOver(history: GameOn, command: pCommand, report: Report.GameOver) extends History {
    /** Final score */
    def score: Int = report.score

  }

}