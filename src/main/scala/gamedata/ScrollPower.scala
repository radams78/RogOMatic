package gamedata

/** A power that a scroll can have */
sealed trait ScrollPower

object ScrollPower {

  case object REMOVE_CURSE extends ScrollPower

}
