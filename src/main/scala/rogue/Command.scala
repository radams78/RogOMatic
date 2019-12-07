package rogue

/** A move that can be made by the player in Rogue. */
sealed trait Command {
  /** Keypress to send to Rogue to execute command */
  val keypress: Char

}

object Command {

  /** Move right */
  case object RIGHT extends Command {
    override val keypress: Char = 'l'
  }

  case object UP extends Command {
    override val keypress: Char = 'j'
  }

  case object REST extends Command {
    override val keypress: Char = '.'
  }

  case object DOWN extends Command {
    override val keypress: Char = 'k'
  }

  case object DOWNRIGHT extends Command {
    override val keypress: Char = 'n'
  }

  case object UPLEFT extends Command {
    override val keypress: Char = 'y'
  }

  case object UPRIGHT extends Command {
    override val keypress: Char = 'u'
  }

  case object LEFT extends Command {
    override val keypress: Char = 'h'
  }

}
