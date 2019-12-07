package rogue

import gamedata.Inventory

class RoguePlayer(rogue: IRogue) {
  def sendCommand(command: Command): Unit = rogue.sendKeypress(command.keypress)

  def gameOver: Boolean = false

  def getInventory: Inventory = {
    rogue.sendKeypress('i')
    val screen: String = rogue.getScreen
    rogue.sendKeypress(' ')
    Inventory.parseInventoryScreen(screen).getOrElse(throw new Error(s"Could not parse inventory screen: $screen")) // TODO Better error handling?
  }

  def getScreen: String = rogue.getScreen

  def start(): Unit = rogue.start()

}
