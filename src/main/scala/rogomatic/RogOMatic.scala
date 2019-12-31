package rogomatic

import expert.Transparent
import rogue.{Recorder, Rogue, RogueActuator}
import view.TextView

/** Entry point for the system */
object RogOMatic extends App {
  val rogue = new Rogue
  val view = new TextView
  val recorder = new Recorder
  val player = new RogueActuator(rogue, recorder)
  new Transparent(player, recorder, view).playRogue()
  rogue.close()
  System.exit(0)
}
