package controller

import gamedata.Command
import model.IRoguePlayer

/** A controller for Rog-O-Matic 
 * 
 * @param player The [[IRoguePlayer]] that commands are sent to */
class Controller(player: IRoguePlayer) extends IController {
  override def performCommand(command: Command): Unit = ()
}
