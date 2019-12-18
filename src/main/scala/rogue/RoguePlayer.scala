package rogue

import gamedata.{Inventory, Scroll, ScrollPower}

import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
trait RoguePlayer {
  val gameOver: Boolean
}

object RoguePlayer {
  def apply(rogue: IRogue): NotStarted = new NotStarted(rogue)

  /** Game of Rogue is not yet started */
  class NotStarted(rogue: IRogue) extends RoguePlayer {
    override val gameOver: Boolean = false

    /** Start the game */
    def start(): GameOn = {
      rogue.start()
      new GameOn(rogue, Map())
    }
  }

  /** Game of Rogue is in progress */
  class GameOn(rogue: IRogue, powers: Map[String, ScrollPower]) extends RoguePlayer {
    def getPowers: Map[String, ScrollPower] = powers

    override val gameOver: Boolean = false

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
            case Command.Read(slot) =>
              lastInventory.items.get(slot) match {
                case Some(s: Scroll) => new GameOn(rogue, powers.updated(s.title, ScrollPower.REMOVE_CURSE))
                case Some(i) => return Left(s"Last command was to read a non-scroll: $i")
                case None => return Left(s"Last command was to read an object that does not exist, slot $slot")
              }
          }
        } else this
      }
    }
  }

  /** Game of Rogue has ended */
  class GameOver(rogue: IRogue) extends RoguePlayer {
    private val scoreRegex: Regex = """(\d+) gold""".r.unanchored

    def getScore: Int = {
      rogue.getScreen match {
        case scoreRegex(score) => score.toInt
      }
    }

    override val gameOver: Boolean = true
  }

}