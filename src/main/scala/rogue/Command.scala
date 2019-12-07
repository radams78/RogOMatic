package rogue

/** A move that can be made by the player in Rogue. */
sealed trait Command {
  /** Keypress to send to Rogue to execute command */
  val keypress: Char

}

object Command {

  /** Move up */
  case object UP extends Command {
    override val keypress: Char = 'j'
  }

  /** Move down */
  case object DOWN extends Command {
    override val keypress: Char = 'k'
  }

  /** Move left */
  case object LEFT extends Command {
    override val keypress: Char = 'h'
  }

  /** Move right */
  case object RIGHT extends Command {
    override val keypress: Char = 'l'
  }

  /** Move up left */
  case object UPLEFT extends Command {
    override val keypress: Char = 'y'
  }

  /** Move up right */
  case object UPRIGHT extends Command {
    override val keypress: Char = 'u'
  }

  /** Move down right */
  case object DOWNRIGHT extends Command {
    override val keypress: Char = 'n'
  }

  /** Rest */
  case object REST extends Command {
    override val keypress: Char = '.'
  }

  object DOWNLEFT extends Command {
    override val keypress: Char = 'b'
  }

}
