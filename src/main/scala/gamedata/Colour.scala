package gamedata

sealed trait Colour

object Colour {
  def parse(colour: String): Colour = colour match {
    case "blue" => BLUE
    case "red" => RED
    case "green" => GREEN
    case "grey" => GREY
    case "brown" => BROWN
    case "clear" => CLEAR
    case "pink" => PINK
    case "white" => WHITE
    case "purple" => PURPLE
    case "black" => BLACK
    case "yellow" => YELLOW
    case "plaid" => PLAID
    case "burgundy" => BURGUNDY
    case "beige" => BEIGE
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