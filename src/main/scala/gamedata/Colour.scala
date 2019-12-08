package gamedata

sealed trait Colour

object Colour {
  def parse(colour: String): Either[String, Colour] = colour match {
    case "blue" => Right(BLUE)
    case "red" => Right(RED)
    case "green" => Right(GREEN)
    case "grey" => Right(GREY)
    case "brown" => Right(BROWN)
    case "clear" => Right(CLEAR)
    case "pink" => Right(PINK)
    case "white" => Right(WHITE)
    case "purple" => Right(PURPLE)
    case "black" => Right(BLACK)
    case "yellow" => Right(YELLOW)
    case "plaid" => Right(PLAID)
    case "burgundy" => Right(BURGUNDY)
    case "beige" => Right(BEIGE)
    case _ => Left(s"Unrecognised colour: $colour")
  }

  case object BLUE extends Colour

  case object RED extends Colour

  case object GREEN extends Colour

  case object GREY extends Colour

  case object BROWN extends Colour

  case object CLEAR extends Colour

  case object PINK extends Colour

  case object WHITE extends Colour

  case object PURPLE extends Colour

  case object BLACK extends Colour

  case object YELLOW extends Colour

  case object PLAID extends Colour

  case object BURGUNDY extends Colour

  case object BEIGE extends Colour

}