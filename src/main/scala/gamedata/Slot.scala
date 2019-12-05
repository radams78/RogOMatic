package gamedata

/** The set of slots in the PC's inventory */
sealed trait Slot

object Slot {

  case object A extends Slot

  case object B extends Slot

  case object C extends Slot

  case object D extends Slot

  case object E extends Slot

}