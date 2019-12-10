package rogue

import gamedata.Inventory

/** High-level communication with the game of Rogue */
class RoguePlayer(rogue: IRogue) {
  private var _gameOver: Boolean = false

  /** Send a command to Rogue */
  def sendCommand(command: Command): Unit = {
    for (k <- command.keypresses) rogue.sendKeypress(k)
    if (!rogue.getScreen.split("\n").last.exists(_ != ' ')) {
      _gameOver = true
    }
  }

  /** True if the game is over */
  def gameOver: Boolean = _gameOver

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

  /** Start the game */
  // TODO What to do if game is already started?
  def start(): Unit = rogue.start()

}
