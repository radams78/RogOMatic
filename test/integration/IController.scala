package integration

import gamedata.Command

/** A controller for Rog-O-Matic */
trait IController {
  def performCommand(command: Command): Unit
}
