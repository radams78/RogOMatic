package rogomatic

import expert.Transparent
import rogue.{IRogue, Recorder, Rogue, RogueActuator}
import view.{IView, TextView}

/** Entry point for the system */
object RogOMatic extends App {
  def playTransparentGame(rogue: IRogue, view: IView): Unit = {
    val recorder: Recorder = new Recorder
    val player: RogueActuator = new RogueActuator(rogue, recorder)
    new Transparent(player, recorder, view).playRogue()
  }

  val rogue = new Rogue
  val view = new TextView
  try {
    playTransparentGame(rogue, view)
  } finally {
    rogue.close()
  }
  System.exit(0)
}
