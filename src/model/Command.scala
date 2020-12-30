package model

import model.rogue.IRogue

/** A command in the game of Rogue */
trait Command {
  /** Send the command to the given Rogue process
   *
   * @param rogue The Rogue process to send the command to */
  def perform(rogue: IRogue): Unit
}

object Command {
  private class KeypressCommand(keypresses : Seq[Char]) extends Command {
    override def perform(rogue: IRogue): Unit = keypresses.foreach(rogue.sendKeypress)
  }

  /** Command to quit the game */
  val QUIT : Command = new KeypressCommand(Seq('Q', 'y', ' ', ' '))

  /** Command to move left */
  val LEFT : Command = new KeypressCommand(Seq('h'))
}