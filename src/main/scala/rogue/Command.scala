package rogue

import gamedata.{Slot, pCommand}

trait Command {
  def topCommand: pCommand

  def keypresses: Seq[Char]

}

object Command {

  object RIGHT extends Command {
    override def topCommand: pCommand = pCommand.RIGHT

    override def keypresses: Seq[Char] = Seq('l')
  }

  object REST extends Command {
    override def topCommand: pCommand = pCommand.REST

    override def keypresses: Seq[Char] = Seq('.')
  }

  case class Read(slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Read(slot)

    override def keypresses: Seq[Char] = Seq('r', slot.label)
  }

}