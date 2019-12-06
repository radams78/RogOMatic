package gamedata

/** The set of slots in the PC's inventory */
sealed trait Slot

object Slot {
  def parse(slot: String): Slot = slot match {
    case "a" => A
    case "b" => B
    case "c" => C
    case "d" => D
    case "e" => E
  }


  case object A extends Slot

  case object B extends Slot

  case object C extends Slot

  case object D extends Slot

  case object E extends Slot

}