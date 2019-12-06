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
    case 'l' => controller.sendCommand(Command.RIGHT)
  }
  rogue.close()
  System.exit(0)
}
