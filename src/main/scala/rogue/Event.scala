package rogue

import gamedata.ProvidesKnowledge._
import gamedata.item.magic.potion.{Potion, PotionPower}
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.{ProvidesKnowledge, pCommand}
import gamestate.pGameState

import scala.util.matching.Regex

/** An event indicated by a message in the message line */
object Event extends Enumeration {
  type Event = Value

  implicit def toValue(x: Value): Val = x.asInstanceOf[Val]

  protected case class Val(message: Regex, inference: pGameState) extends super.Val

  implicit def providesKnowledge: ProvidesKnowledge[Event] = (self: Event) => self.inference.implications

  /** Empty message line */
  val NONE: Event = Val("^ *$".r, pGameState())

  /** PC picked up a pile of gold */
  val GOLD: Event = Val("""(\d+) pieces of gold""".r.unanchored, pGameState())

  /** PC quaffed a potion of healing */
  val HEALING: Event =
    Val("""you begin to feel better""".r.unanchored, pGameState(pCommand.Quaff(Potion(PotionPower.HEALING))))

  /** Monster attacked PC and missed */
  val MISSED_BY: Event = Val("""the (.*) misses""".r.unanchored, pGameState()) // TODO Monster is awake

  /** PC picked up an item */
  val PICKED_UP: Event = Val("""(.*) \(\w\)""".r.unanchored, pGameState()) // TODO Do anything here?

  /** PC read a scroll of remove curse */
  val REMOVE_CURSE: Event = Val(
    """you feel as though someone is watching over you""".r.unanchored,
    pGameState(pCommand.Read(Scroll(ScrollPower.REMOVE_CURSE)))
  )

  /** Given the top line of a screen from Rogue, return the corresponding [[Event]] if there is one; otherwise
   * returns an error message */
  def interpretMessage(messageLine: String): Either[String, Event] =
    Event.values
      .unsorted
      .map((event: Event) => event.message
        .findFirstMatchIn(messageLine)
        .map((_: Regex.Match) => Right(event)))
      .find(_.nonEmpty)
      .flatten
      .getOrElse(Left(s"Unrecognised event: $messageLine"))
}
