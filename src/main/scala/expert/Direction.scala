package expert

/** The eight directions */
trait Direction {
  /** Keypress to send this direction to Rogue as a command or part of a command */
  val keypress: Char

}

object Direction {

  case object DOWNLEFT extends Direction {
    override val keypress: Char = 'b'
  }

  case object LEFT extends Direction {
    override val keypress: Char = 'h'
  }

  case object UP extends Direction {
    override val keypress: Char = 'j'
  }

  case object DOWN extends Direction {
    override val keypress: Char = 'k'
  }

  case object RIGHT extends Direction {
    override val keypress: Char = 'l'
  }

  case object DOWNRIGHT extends Direction {
    override val keypress: Char = 'n'
  }

  case object UPRIGHT extends Direction {
    override val keypress: Char = 'u'
  }

  case object UPLEFT extends Direction {
    override val keypress: Char = 'y'
  }

}