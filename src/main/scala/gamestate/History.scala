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
    /** Current inventory */
    def inventory: pInventory

    /** Last screen displayed by Rogue */
    def screen: String

    /** Add a move to the history */
    def nextMove(cmd: pCommand, report: Report): Either[String, History] = report match {
      case report: Report.GameOn => NextMove.build(this, cmd, report)
      case report: Report.GameOver => Right(GameOver(this, cmd, report))
    }

    /** Set of all facts we can deduce from the history */
    def knowledge: Set[Fact]

    /** Last command performed, or None if it is before the first move of the game */
    def lastCommand: Option[pCommand]
  }

  object GameOn {
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self.knowledge
  }

  /** The history of a game before the first move is made */
  case class FirstMove(report: Report.GameOn) extends GameOn {
    override def screen: String = report.screen

    override def inventory: pInventory = report.inventory

    override def knowledge: Set[Fact] = report.implications

    override def lastCommand: Option[pCommand] = None
  }

  /** The history of a game with one or more moves that is not yet finished */
  case class NextMove(history: GameOn, command: pCommand, report: Report.GameOn, override val inventory: pInventory) extends GameOn {
    override def screen: String = report.screen

    override def knowledge: Set[Fact] = history.knowledge.union(
      report.implications.union(
        inventory.implications.union(
          command.implications
        )
      )
    )

    override def lastCommand: Option[pCommand] = Some(command)
  }

  object NextMove {
    def build(history: GameOn, command: pCommand, report: Report.GameOn): Either[String, NextMove] = for {
      i2 <- history.inventory.infer(command)
      i3 <- i2.merge(report.inventory)
      i4 <- i3.infer(report) // TODO Ugly?

    } yield NextMove(history, command, report, i4)

  }

  /** The complete history of a finished game of Rogue */
  case class GameOver(history: GameOn, command: pCommand, report: Report.GameOver) extends History {
    /** Final score */
    def score: Int = report.score

  }

}