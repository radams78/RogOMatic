package rogue

import gamedata.Inventory

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
      if (rogue.getScreen.split("\n").head.contains("-more-")) {
        rogue.sendKeypress(' ')
      }
      if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
        new GameOver(rogue)
      } else this
    }
  }

  /** Game of Rogue has ended */
  class GameOver(rogue: IRogue) extends RoguePlayer {
    override val gameOver: Boolean = true
  }

}