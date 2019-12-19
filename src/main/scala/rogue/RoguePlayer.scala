package rogue

import gamedata.{Inventory, ScrollPower}

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
      new GameOn(rogue, Map())
    }
  }

  /** Game of Rogue is in progress */
  class GameOn(rogue: IRogue, powers: Map[String, ScrollPower]) extends RoguePlayer {
    /** Known powers of scrolls */
    def getPowers: Map[String, ScrollPower] = powers

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
      for (lastInventory <- getInventory) yield {
        for (k <- command.keypresses) rogue.sendKeypress(k)
        if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
          return Right(new GameOver(rogue))
        }
        if (rogue.getScreen.split("\n").head.contains("-more-")) {
          rogue.sendKeypress(' ')
        }
        if (rogue.getScreen.split("\n").head.contains("you feel as though someone is watching over you")) {
          command match {
            case Command.Read(slot, scroll) => new GameOn(rogue, powers.updated(scroll.title, ScrollPower.REMOVE_CURSE))
            case cmd => return Left {
              "Received remove curse message but did not read scroll"
            }
          }
        } else this
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