package rogomatic

import rogue.{Command, Rogue}
import view.TextView

import scala.io.StdIn

/** Entry point for the system */
object RogOMatic extends App {
  val rogue = new Rogue
  val view = new TextView
  val controller = new Controller(rogue, view)
  controller.startTransparent()
  while (!controller.gameOver) {
    var cmd: Char = StdIn.readChar()
    cmd match {
      case 'j' => controller.sendCommand(Command.UP)
      case 'k' => controller.sendCommand(Command.DOWN)
      case 'l' => controller.sendCommand(Command.RIGHT)
      case 'n' => controller.sendCommand(Command.DOWNRIGHT)
      case 'u' => controller.sendCommand(Command.UPRIGHT)
      case 'y' => controller.sendCommand(Command.UPLEFT)
      case '.' => controller.sendCommand(Command.REST)
    }
  }
  rogue.close()
  System.exit(0)
}
