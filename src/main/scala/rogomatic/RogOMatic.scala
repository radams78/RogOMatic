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
  try {
    new Transparent(player, recorder, view).playRogue()
  } finally {
    rogue.close()
  }
  System.exit(0)
}
