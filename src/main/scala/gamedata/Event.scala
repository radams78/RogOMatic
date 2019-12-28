package gamedata

import rogue.Command

/** An event indicated by a message in the message line */
object Event extends Enumeration {
  type Event = Value

  /** PC quaffed a potion of healing */
  val HEALING: Event = Val("you begin to feel better",
    GameState(Command.Quaff(Potion(PotionPower.HEALING))))

  /** PC read a scroll of remove curse */
  val REMOVE_CURSE: Event = Val("you feel as though someone is watching over you",
    GameState(Command.Read(Scroll(ScrollPower.REMOVE_CURSE))))

  implicit def toValue(x: Value): Val = x.asInstanceOf[Val]

  protected case class Val(message: String, inference: GameState) extends super.Val

}
