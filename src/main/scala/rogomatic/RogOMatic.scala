package rogomatic

import rogue.{Command, Rogue, RoguePlayer}
import view.TextView

import scala.io.StdIn

/** Entry point for the system */
object RogOMatic extends App {
  val rogue = new Rogue
  val view = new TextView
  val player = new RoguePlayer(rogue)
  player.start()
  while (!player.gameOver) {
    view.displayScreen(player.getScreen)
    view.displayInventory(player.getInventory)
    var cmd: Char = StdIn.readChar()
    cmd match {
      case 'j' => player.sendCommand(Command.UP)
      case 'k' => player.sendCommand(Command.DOWN)
      case 'l' => player.sendCommand(Command.RIGHT)
      case 'n' => player.sendCommand(Command.DOWNRIGHT)
      case 'u' => player.sendCommand(Command.UPRIGHT)
      case 'y' => player.sendCommand(Command.UPLEFT)
      case '.' => player.sendCommand(Command.REST)
    }
  }
  rogue.close()
  System.exit(0)
}
