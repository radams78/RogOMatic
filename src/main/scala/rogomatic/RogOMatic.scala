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
  var cmd = StdIn.readChar()
  cmd match {
    case 'j' => controller.sendCommand(Command.UP)
    case 'k' => controller.sendCommand(Command.DOWN)
    case 'l' => controller.sendCommand(Command.RIGHT)
    case '.' => controller.sendCommand(Command.REST)
  }
  rogue.close()
  System.exit(0)
}
