package controller

import gamedata.Command

/** A controller for Rog-O-Matic */
trait IController {
  /** Send the given command to Rog-O-Matic */
  def performCommand(command: Command): Unit
}
