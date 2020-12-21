package gamedata

import rogue.IRogue

trait Command {
  def perform(rogue: IRogue): Unit
}

object Command {
  private class KeypressCommand(keypresses : Seq[Char]) extends Command {
    override def perform(rogue: IRogue): Unit = keypresses.foreach(rogue.sendKeypress)
  }

  val QUIT : Command = new KeypressCommand(Seq('Q', 'y', ' '))

  val LEFT : Command = new KeypressCommand(Seq('h'))
}