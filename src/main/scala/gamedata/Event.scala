package gamedata

import rogue.Command

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/** An event indicated by a message in the message line */
object Event extends Enumeration {
  type Event = Value

  val GOLD: Event = Val("""(\d+) pieces of gold""".r.unanchored, (_: Match) => Right(GameState()))

  /** PC quaffed a potion of healing */
  val HEALING: Event = Val("""you begin to feel better""".r.unanchored,
    (_: Match) => GameState.build(Command.Quaff(Potion(PotionPower.HEALING))))

  val MISSED_BY: Event = Val("""the (.*) misses""".r.unanchored, (_: Match) => Right(GameState())) // TODO Monster is awake

  /** PC read a scroll of remove curse */
  val REMOVE_CURSE: Event = Val("""you feel as though someone is watching over you""".r.unanchored,
    (_: Match) => GameState.build(Command.Read(Scroll(ScrollPower.REMOVE_CURSE))))

  implicit def toValue(x: Value): Val = x.asInstanceOf[Val]

  def interpretMessage(messageLine: String): Either[String, GameState] = {
    if (messageLine.forall(_ == ' ')) Right(GameState())
    else Event.values
      .unsorted
      .map((event: Event) => event.message
        .findFirstMatchIn(messageLine)
        .map(event.inference))
      .find(_.nonEmpty)
      .flatten
      .getOrElse(Left(s"Unrecognised event: $messageLine"))
  }

  protected case class Val(message: Regex, inference: Match => Either[String, GameState]) extends super.Val

}
