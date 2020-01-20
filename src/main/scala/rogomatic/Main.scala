package rogomatic

import _root_.view.TextView
import rogue._

/** Entry point for the system */
object Main extends App {
  val rogue = new Rogue
  val view = new TextView
  try {
    RogOMatic.transparent(rogue, view).playRogue(view)
  } finally {
    rogue.close()
  }
  System.exit(0)
}


