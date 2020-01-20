package rogomatic

import _root_.view.{IView, TextView}
import expert.{Expert, Transparent}
import gamestate.IRecorder
import rogue._

import scala.annotation.tailrec

/** Entry point for the system */
object Main extends App {
  def playTransparentGame(rogue: IRogue, view: IView): Unit = {
    val recorder: Recorder = new Recorder
    val player: IRogueActuator = new RogueActuator(rogue, recorder)
    val expert: Expert = new Transparent(view)
    player.start()
    new RogOMatic(recorder, player, expert).playRogue(view)
  }

  @tailrec
  private def playRogue0(recorder: IRecorder, player: IRogueActuator, expert: Expert): Unit = {
    if (recorder.gameOver) {
      view.displayGameOver(recorder.getScore)
    } else {
      player.sendCommand(expert.advice(recorder.gameState)) match {
        case Left(err) => view.displayError(err)
        case Right(_) => playRogue0(recorder, player, expert)
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

class RogOMatic(recorder: IRecorder, player: IRogueActuator, expert: Expert) {
  @tailrec
  final def playRogue(view: IView): Unit = {
    if (recorder.gameOver) {
      view.displayGameOver(recorder.getScore)
    } else {
      player.sendCommand(expert.advice(recorder.gameState)) match {
        case Left(err) => view.displayError(err)
        case Right(_) => playRogue(view)
      }
    }
  }
}