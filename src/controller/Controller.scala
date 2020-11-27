package controller

import gamedata.Command
import rogue.IRogue

class Controller(rogue : IRogue) {
  def performCommand(command: Command): Unit = {
    rogue.sendKeypress('Q')
    rogue.sendKeypress('y')
    rogue.sendKeypress(' ')
    rogue.sendKeypress(' ')
  }

}
