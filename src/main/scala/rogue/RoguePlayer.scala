package rogue

import gamedata.Domain._
import gamedata.{GameState, Inventory, PotionKnowledge, ScrollKnowledge}

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
      new GameOn(rogue, new GameState(new ScrollKnowledge(Map()), new PotionKnowledge(Map()), None))
    }
  }

  /** Game of Rogue is in progress */
  class GameOn(rogue: IRogue, gameState: GameState) extends RoguePlayer {
    /** Known powers of scrolls */
    def getScrollKnowledge: ScrollKnowledge = gameState.scrollKnowledge

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

    private def update(lastCommand: Command): Either[String, RoguePlayer] = {
      if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
        return Right(new GameOver(rogue))
      }
      rogue.getScreen.split("\n")(0) match {
        case moreRegex(message) =>
          rogue.sendKeypress(' ')
          for (gs <- new GameState(gameState.scrollKnowledge, gameState.potionKnowledge, Some(lastCommand))
            .merge(GameState.interpretMessage(message))) yield new GameOn(rogue, gs)
        case message =>
          for (gs <- new GameState(gameState.scrollKnowledge, gameState.potionKnowledge, Some(lastCommand))
            .merge(GameState.interpretMessage(message))) yield new GameOn(rogue, gs) // TODO Duplication
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