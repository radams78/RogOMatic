package integration

import rogue.{IRogue, ISensor, Sensor}

class Controller(model: IRoguePlayer) extends IController {
  override def performCommand(command: Command): Unit = ()
}
