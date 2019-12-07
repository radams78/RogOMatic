package rogomatic

import expert.Transparent
import rogue.{Rogue, RoguePlayer}
import view.TextView

/** Entry point for the system */
object RogOMatic extends App {
  val rogue = new Rogue
  val view = new TextView
  val player = new RoguePlayer(rogue)
  new Transparent(player, view)
  rogue.close()
  System.exit(0)
}
