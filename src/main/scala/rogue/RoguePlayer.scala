package rogue

import gamedata.{Inventory, ScrollPower}

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
      new GameOn(rogue)
    }
  }

  /** Game of Rogue is in progress */
  class GameOn(rogue: IRogue) extends RoguePlayer {
    def getPowers: Map[String, ScrollPower] = Map("coph rech" -> ScrollPower.REMOVE_CURSE)

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
    def sendCommand(command: Command): RoguePlayer = {
      for (k <- command.keypresses) rogue.sendKeypress(k)
      if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
        return new GameOver(rogue)
      }
      if (rogue.getScreen.split("\n").head.contains("-more-")) {
        rogue.sendKeypress(' ')
      }
      this
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