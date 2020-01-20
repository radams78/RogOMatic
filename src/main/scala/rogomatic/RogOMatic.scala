package rogomatic

import expert.{Expert, Transparent}
import gamestate.IRecorder
import rogue.{IRogue, IRogueActuator, Recorder, RogueActuator}
import view.IView

import scala.annotation.tailrec

class RogOMatic(recorder: IRecorder, player: IRogueActuator, expert: Expert) {
  final def playRogue(view: IView): Unit = {
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

    player.start()
    playRogue0()
  }
}

object RogOMatic {
  def transparent(rogue: IRogue, view: IView): RogOMatic = {
    val recorder: IRecorder = new Recorder
    val player: IRogueActuator = new RogueActuator(rogue, recorder)
    val expert: Expert = new Transparent(view)
    new RogOMatic(recorder, player, expert)
  }

}