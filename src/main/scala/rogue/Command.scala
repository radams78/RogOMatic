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

  object UP extends Command {
    override val keypress: Char = 'j'
  }

  object REST extends Command {
    override val keypress: Char = '.'
  }

  object DOWN extends Command {
    override val keypress: Char = 'k'
  }

  object DOWNRIGHT extends Command {
    override val keypress: Char = 'n'
  }

}
