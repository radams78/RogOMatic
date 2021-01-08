package controller

import model.Command
import model.rogue.{RogueGame, RoguePlayer}

/** A controller that allows the user to play the game of Rogue in transparent mode.
 * 
 * This takes commands input from StdIn and passes them on to the given [[RoguePlayer]]. If the user enters an invalid
 * command, it prints an erro message and waits for input again.
 * 
 * This class is a humble object. */

// TODO End thread when game is over
class TransparentController(rogueGame: RogueGame) extends Runnable {
  override def run(): Unit = while (true) {
    val input: Char = scala.io.StdIn.readChar()
    input match {
      case 'Q' => rogueGame.performCommand(Command.QUIT)
      case _ => System.out.println("Unrecognized command")
    }
  }
}
