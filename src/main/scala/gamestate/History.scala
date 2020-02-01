package gamestate

import gamedata._
import gamedata.fact.ProvidesKnowledge._
import gamedata.fact.UsesKnowledge._
import gamedata.fact.{Fact, ProvidesKnowledge}
import gamedata.item.magic.potion.Potion.Potion
import gamedata.item.magic.scroll.Scroll.Scroll
import rogue.Command

/** The history of the game, holding all commands sent to Rogue and all reports retrieved from Rogue */
trait History

object History {

  /** The history of a game that is not yet finished */
  trait GameOn extends History {
    /** Fill in as much information as possible about the given command */
    def elaborate(command: Command): Either[String, pCommand] = command match {
      case Command.UP => Right(pCommand.UP)
      case Command.DOWN => Right(pCommand.DOWN)
      case Command.LEFT => Right(pCommand.LEFT)
      case Command.RIGHT => Right(pCommand.RIGHT)
      case Command.UPLEFT => Right(pCommand.UPLEFT)
      case Command.UPRIGHT => Right(pCommand.UPRIGHT)
      case Command.DOWNLEFT => Right(pCommand.DOWNLEFT)
      case Command.DOWNRIGHT => Right(pCommand.DOWNRIGHT)
      case Command.REST => Right(pCommand.REST)
      case Command.DOWNSTAIRS => Right(pCommand.DOWNSTAIRS)
      case Command.Quaff(slot) => inventory.item(slot) match {
        case Some(potion: Potion) => Right(pCommand.Quaff(slot, potion))
        case Some(item) => Left(s"Tried to quaff $item")
        case None => Left(s"Tried to quaff empty slot")
      }
      case Command.Read(slot) => inventory.item(slot) match {
        case Some(scroll: Scroll) => Right(pCommand.Read(slot, scroll))
        case Some(item) => Left(s"Tried to read $item")
        case None => Left(s"Tried to read empty slot")
      }
      case Command.Throw(dir, slot) => inventory.item(slot) match {
        case Some(item) => Right(pCommand.Throw(dir, slot, item))
        case None => Left(s"Tried to throw empty slot")
      }
      case Command.Wield(slot) => Right(pCommand.Wield(slot))
    }

    /** Facts that are known to be true after the following command is performed */
    def implicationsAfter(command: pCommand): Either[String, Set[Fact]] =
      _implications.foldLeft[Either[String, Set[Fact]]](Right(command.implications))({
        case (acc, fact) => for {
          f1 <- acc
          f2 <- fact.after(command)
        } yield f1 ++ f2
      })

    /** Current inventory */
    def inventory: Inventory

    /** Last screen displayed by Rogue */
    def screen: String

    /** Add a move to the history */
    def nextMove(cmd: Command, report: Report): Either[String, History] = report match {
      case report: Report.GameOn => NextMove.build(this, cmd, report)
      case report: Report.GameOver => Right(GameOver(this, cmd, report))
    }

    /** Set of all facts we can deduce from the history */
    protected def _implications: Set[Fact]

    /** Last command performed, or None if it is before the first move of the game */
    def lastCommand: Option[pCommand]
  }

  object GameOn {
    implicit def providesKnowledge: ProvidesKnowledge[GameOn] = (self: GameOn) => self._implications
  }

  /** The history of a game before the first move is made */
  case class FirstMove(report: Report.GameOn) extends GameOn {
    override def screen: String = report.screen

    override def inventory: Inventory = report.inventory

    override def _implications: Set[Fact] = report.implications

    override def lastCommand: Option[pCommand] = None
  }

  /** The history of a game with one or more moves that is not yet finished */
  case class NextMove(history: GameOn, command: pCommand, report: Report.GameOn, override val inventory: Inventory, override val _implications: Set[Fact]) extends GameOn {
    override def screen: String = report.screen

    override def lastCommand: Option[pCommand] = Some(command)
  }

  object NextMove {
    def build(history: GameOn, command: Command, report: Report.GameOn): Either[String, NextMove] = for {
      lastCommand <- history.elaborate(command)
      lastCommand <- lastCommand.merge(report.lastCommand)
      facts <- history.implicationsAfter(lastCommand)
      inventory <- report.inventory.infer(facts)
      inventory <- inventory.infer(lastCommand)
    } yield NextMove(history, lastCommand, report, inventory,
      facts.union(report.implications.union(inventory.implications.union(lastCommand.implications)))) // TODO Extract
  }

  /** The complete history of a finished game of Rogue */
  case class GameOver(history: GameOn, command: Command, report: Report.GameOver) extends History {
    /** Final score */
    def score: Int = report.score

  }

}