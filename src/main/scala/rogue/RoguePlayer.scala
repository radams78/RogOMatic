package rogue

import gamedata.Inventory

/** High-level communication with the game of Rogue */
class RoguePlayer(rogue: IRogue) {
  /** Send a command to Rogue */
  def sendCommand(command: Command): Unit = rogue.sendKeypress(command.keypress)

  /** True if the game is over */
  def gameOver: Boolean = false

  /** Current inventory */
  def getInventory: Inventory = {
    rogue.sendKeypress('i')
    val screen: String = rogue.getScreen
    rogue.sendKeypress(' ')
    Inventory.parseInventoryScreen(screen).getOrElse(throw new Error(s"Could not parse inventory screen: $screen")) // TODO Better error handling?
  }

  /** Current screen being displayed by Rogue */
  def getScreen: String = rogue.getScreen

  /** Start the game */
  // TODO What to do if game is already started?
  def start(): Unit = rogue.start()

}
