package rogue

import gamedata.{Direction, Slot, pCommand}

/** A command that can be sent to [[IRogueActuator]] */
trait Command {
  /** _.topCommand is the embedding of Command in pCommand */
  def topCommand: pCommand

  /** Keypresses to be sent to Rogue to perform this command */
  def keypresses: Seq[Char]
}

object Command {

  /** Move up */
  case object UP extends Command {
    override def topCommand: pCommand = pCommand.UP

    override def keypresses: Seq[Char] = Seq('j')
  }

  /** Move down */
  case object DOWN extends Command {
    override def topCommand: pCommand = pCommand.DOWN

    override def keypresses: Seq[Char] = Seq('k')
  }

  /** Move left */
  case object LEFT extends Command {
    override def topCommand: pCommand = pCommand.LEFT

    override def keypresses: Seq[Char] = Seq('h')
  }

  /** Move right */
  object RIGHT extends Command {
    override def topCommand: pCommand = pCommand.RIGHT

    override def keypresses: Seq[Char] = Seq('l')
  }

  /** Move up and left */
  case object UPLEFT extends Command {
    override def topCommand: pCommand = pCommand.UPLEFT

    override def keypresses: Seq[Char] = Seq('y')
  }

  /** Move up and right */
  case object UPRIGHT extends Command {
    override def topCommand: pCommand = pCommand.UPRIGHT

    override def keypresses: Seq[Char] = Seq('u')
  }

  /** Move down and left */
  case object DOWNLEFT extends Command {
    override def topCommand: pCommand = pCommand.DOWNLEFT

    override def keypresses: Seq[Char] = Seq('b')
  }

  /** Move down and right */
  case object DOWNRIGHT extends Command {
    override def topCommand: pCommand = pCommand.DOWNRIGHT

    override def keypresses: Seq[Char] = Seq('n')
  }

  /** Rest for one turn */
  object REST extends Command {
    override def topCommand: pCommand = pCommand.REST

    override def keypresses: Seq[Char] = Seq('.')
  }

  /** Go downstairs */
  case object DOWNSTAIRS extends Command {
    override def topCommand: pCommand = pCommand.DOWNSTAIRS

    override def keypresses: Seq[Char] = Seq('>')
  }

  /** Quaff a potion */
  case class Quaff(slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Quaff(slot)

    override def keypresses: Seq[Char] = Seq('q', slot.label)
  }

  /** Read a scroll */
  case class Read(slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Read(slot)

    override def keypresses: Seq[Char] = Seq('r', slot.label)
  }

  /** Throw an object */
  case class Throw(dir: Direction, slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Throw(dir, slot)

    override def keypresses: Seq[Char] = Seq('t', dir.keypress, slot.label)
  }

  /** Wield an object */
  case class Wield(slot: Slot) extends Command {
    override def topCommand: pCommand = pCommand.Wield(slot)

    override def keypresses: Seq[Char] = Seq('w', slot.label)
  }

}