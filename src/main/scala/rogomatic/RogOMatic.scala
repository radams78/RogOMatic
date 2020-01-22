package rogomatic

import expert.{Expert, Transparent}
import gamedata.pCommand
import gamestate.History
import rogue._
import view.IView

import scala.annotation.tailrec

/** Mediator class between the actuator and the expert. RogOMatic sends the current state of the game to expert
 * which picks the next move. RogOMatic then sends the move to the actuator, which returns with a report giving
 * information about the next state of the game. RogOMatic updates the state of the game and sends the new state
 * back to expert. This process is repeated until the game is over. */
class RogOMatic(actuator: IRogueActuator, expert: Expert) {
  /** Play a complete game of Rogue from start ot end, with output being sent to view */
  final def playRogue(view: IView): Unit = {
    @tailrec
    def playRogue0(history: History): Unit = history match {
      case gameOver: History.GameOver => view.displayGameOver(gameOver.score)
      case gameOn: History.GameOn =>
        val cmd: pCommand = expert.advice(gameOn)
        (for {
          report <- actuator.sendCommand(cmd)
          history <- gameOn.nextMove(cmd, report)
        } yield history) match {
          case Right(history) => playRogue0(history)
          case Left(err) => view.displayError(err)
        }
    }

    actuator.start() match {
      case Left(err) => view.displayError(err)
      case Right(report) => playRogue0(History.FirstMove(report))
    }
  }
}

object RogOMatic {
  /** Play a transparent game of Rogue */
  def transparent(rogue: IRogue, view: IView): RogOMatic = {
    val player: IRogueActuator = new RogueActuator(rogue)
    val expert: Expert = new Transparent(view)
    new RogOMatic(player, expert)
  }
}


