package gamedata

/** The colours that a potion can have */
sealed trait Colour

object Colour {
  val ALL: Set[Colour] =
    Set(BLUE, RED, GREEN, GREY, BROWN, CLEAR, PINK, WHITE, PURPLE, BLACK, YELLOW, PLAID, BURGUNDY, BEIGE)

  def parse(colour: String): Either[String, Colour] =
    ALL.find(_.toString == colour).map(Right(_)).getOrElse(Left(s"Unrecognised colour: $colour"))

  case object BLUE extends Colour {
    override def toString: String = "blue"
  }

  case object RED extends Colour {
    override def toString: String = "red"
  }

  case object GREEN extends Colour {
    override def toString: String = "green"
  }

  case object GREY extends Colour {
    override def toString: String = "grey"
  }

  case object BROWN extends Colour {
    override def toString: String = "brown"
  }

  case object CLEAR extends Colour {
    override def toString: String = "clear"
  }

  case object PINK extends Colour {
    override def toString: String = "pink"
  }

  case object WHITE extends Colour {
    override def toString: String = "white"
  }

  case object PURPLE extends Colour {
    override def toString: String = "purple"
  }

  case object BLACK extends Colour {
    override def toString: String = "black"
  }

  case object YELLOW extends Colour {
    override def toString: String = "yellow"
  }

  case object PLAID extends Colour {
    override def toString: String = "plaid"
  }

  case object BURGUNDY extends Colour {
    override def toString: String = "burgundy"
  }

  case object BEIGE extends Colour {
    override def toString: String = "beige"
  }

}