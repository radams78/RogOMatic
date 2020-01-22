package rogue

import gamedata.{Slot, pCommand}

/** A command that can be sent to [[IRogueActuator]] */
trait Command {
  /** _.topCommand is the embedding of Command in pCommand */
  def topCommand: pCommand

  /** Keypresses to be sent to Rogue to perform this command */
  def keypresses: Seq[Char]

}

object Command {

  /** Move right */
  object RIGHT extends Command {
    override def topCommand: pCommand = pCommand.RIGHT

    override def keypresses: Seq[Char] = Seq('l')
  }

  /** Rest for one turn */
  object REST extends Command {
    override def topCommand: pCommand = pCommand.REST

    override def keypresses: Seq[Char] = Seq('.')
  }

  /** Read a scroll */
  case class Read(slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Read(slot)

    override def keypresses: Seq[Char] = Seq('r', slot.label)
  }

}