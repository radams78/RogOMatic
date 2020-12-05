package controller

import gamedata.Command
import model.IRoguePlayer

/** A controller for Rog-O-Matic */
class Controller(player: IRoguePlayer) extends IController {
  override def performCommand(command: Command): Unit = ()
}
