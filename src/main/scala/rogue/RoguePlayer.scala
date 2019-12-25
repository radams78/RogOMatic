package rogue

import gamedata.{GameState, Inventory, ScrollPower}

import scala.annotation.tailrec
import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
trait RoguePlayer

object RoguePlayer {
  /** Create a new [[RoguePlayer]] with the given game of Rogue. We assume that the [[IRogue]] object is in its
   * initial state, i.e. the Rogue process has not yet been started. */
  def apply(rogue: IRogue): NotStarted = new NotStarted(rogue)

  /** Game of Rogue is not yet started */
  class NotStarted(rogue: IRogue) extends RoguePlayer {
    /** Start the game */
    def start(): GameOn = {
      rogue.start()
      new GameOn(rogue, new GameState(Map()))
    }
  }

  /** Game of Rogue is in progress */
  class GameOn(rogue: IRogue, gameState: GameState) extends RoguePlayer {
    /** Known powers of scrolls */
    def getPowers: Map[String, ScrollPower] = gameState.scrollPowers

    /** Current inventory */
    def getInventory: Either[String, Inventory] = {
      // TODO Speed-ups possible here:
      // 1. Do not invoke inventory screen every time
      // 2. Make rogue.sendKeypress(' ') asynchronous
      rogue.sendKeypress('i')
      val screen: String = rogue.getScreen
      rogue.sendKeypress(' ')
      Inventory.parseInventoryScreen(screen)
    }

    /** Current screen being displayed by Rogue */
    def getScreen: String = rogue.getScreen

    /** Send a command to Rogue */
    def sendCommand(command: Command): Either[String, RoguePlayer] = {
      for (k <- command.keypresses) rogue.sendKeypress(k)
      update(command)
    }

    private val moreRegex: Regex = """(.*)-more-""".r.unanchored

    @tailrec
    private def update(lastCommand: Command): Either[String, RoguePlayer] = {
      if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
        return Right(new GameOver(rogue))
      }
      rogue.getScreen.split("\n").head match {
        case moreRegex(message) =>
          rogue.sendKeypress(' ')
          gameState.interpretMessage(message, lastCommand) match {
            case Right(gs) => new GameOn(rogue, gs).update(lastCommand)
            case Left(s) => Left(s)
          }
        case message =>
          for (gs <- gameState.interpretMessage(message, lastCommand)) yield new GameOn(rogue, gs)
      }
    }
  }

  /** Game of Rogue has ended */
  class GameOver(rogue: IRogue) extends RoguePlayer {
    private val scoreRegex: Regex = """(\d+) gold""".r.unanchored

    /** Final score */
    def getScore: Int = {
      rogue.getScreen match {
        case scoreRegex(score) => score.toInt
      }
    }
  }

}