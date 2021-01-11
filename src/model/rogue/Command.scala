package model.rogue

/** A command in the game of model.rogue.Rogue */
trait Command {
  /** Send the command to the given model.rogue.Rogue process
   *
   * @param rogue The model.rogue.Rogue process to send the command to */
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