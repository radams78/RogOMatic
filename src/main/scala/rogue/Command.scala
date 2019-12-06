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

}
