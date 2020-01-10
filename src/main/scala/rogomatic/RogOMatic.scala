package rogomatic

import expert.{Expert, Transparent}
import rogue.{IRogue, Recorder, Rogue, RogueActuator}
import view.{IView, TextView}

import scala.annotation.tailrec

/** Entry point for the system */
object RogOMatic extends App {
  def playTransparentGame(rogue: IRogue, view: IView): Unit = {
    val recorder: Recorder = new Recorder
    val player: RogueActuator = new RogueActuator(rogue, recorder)
    val expert: Expert = new Transparent(view)
    player.start()
    playRogue0()

    @tailrec
    def playRogue0(): Unit = {
      if (recorder.gameOver) {
        view.displayGameOver(recorder.getScore)
      } else {
        player.sendCommand(expert.advice(recorder.gameState)) match {
          case Left(err) => view.displayError(err)
          case Right(_) => playRogue0()
        }
      }
    }
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
