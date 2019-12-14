package rogue

import gamedata.{Direction, Slot}

/** A move that can be made by the player in Rogue. */
// TODO Validation 
sealed trait Command {
  /** Keypress to send to Rogue to execute command */
  val keypresses: Seq[Char]

}

object Command {

  /** Wield a weapon */
  case class Wield(slot: Slot) extends Command {
    override val keypresses: Seq[Char] = Seq('w', slot.label)
  }

  /** Move up */
  case object UP extends Command {
    override val keypresses: Seq[Char] = Seq('j')
  }

  /** Move down */
  case object DOWN extends Command {
    override val keypresses: Seq[Char] = Seq('k')
  }

  /** Move left */
  case object LEFT extends Command {
    override val keypresses: Seq[Char] = Seq('h')
  }

  /** Move right */
  case object RIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('l')
  }

  /** Move up left */
  case object UPLEFT extends Command {
    override val keypresses: Seq[Char] = Seq('y')
  }

  /** Move up right */
  case object UPRIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('u')
  }

  /** Move down left */
  object DOWNLEFT extends Command {
    override val keypresses: Seq[Char] = Seq('b')
  }

  /** Move down right */
  case object DOWNRIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('n')
  }

  /** Rest */
  case object REST extends Command {
    override val keypresses: Seq[Char] = Seq('.')
  }

  /** Throw an item */
  case class Throw(dir: Direction, slot: Slot) extends Command {
    override val keypresses: Seq[Char] = Seq('t', dir.keypress, slot.label)
  }

  /** Read a scroll */
  case class Read(slot: Slot) extends Command {
    override val keypresses: Seq[Char] = Seq('r', slot.label)
  }

}
