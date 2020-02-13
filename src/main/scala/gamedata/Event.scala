package gamedata

import gamedata.MonsterType.MonsterType
import gamedata.fact.ProvidesKnowledge
import gamedata.fact.ProvidesKnowledge._
import gamedata.item.magic.potion.{Potion, PotionPower}
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.item.pItem
import rogue.RogueParsers

import scala.language.{implicitConversions, postfixOps}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import scala.util.parsing.combinator._
import scala.util.parsing.input.Reader


/** An event indicated by a message in the message line */
sealed trait Event {
  /** Partial information that we can infer about the previous command given that this event happened */
  def inference: pCommand = pCommand.UNKNOWN
}

object Event extends RegexParsers with Parsers {

  case class Gold(value: Int) extends Event

  case object HEALING extends Event {
    override def inference: pCommand = pCommand.Quaff(Potion(PotionPower.HEALING))
  }

  case class MissedBy(monsterType: MonsterType) extends Event

  private def regexParser[T](regex: Regex, process: (Match, Reader[Char], Reader[Char], String) => ParseResult[T]): Parser[T] = Parser((msg: Input) => {
    val source: CharSequence = msg.source
    val offset: Int = msg.offset
    val start: Int = handleWhiteSpace(source, offset)
    regex.findPrefixMatchOf(source.subSequence(start, source.length())) match {
      case Some(matched) => process(matched, msg.drop(start + matched.end - offset), msg, source.toString)
      case None =>
        val found: String = if (start == source.length()) "end of source" else "'" + source.charAt(start) + "'"
        Failure("string matching regex '" + regex + "' expected but " + found + " found", msg.drop(start - offset))
    }
  })

  implicit def providesKnowledge: ProvidesKnowledge[Event] = (self: Event) => self.inference.implications

  /*  /** Monster attacked PC and missed */
    val MISSED_BY: Event = Val("""the (.*) misses""".r.unanchored, pCommand.UNKNOWN) // TODO Monster is awake
  
    /** PC picked up an item */
    val PICKED_UP: Event = Val("""(.*) \(\w\)""".r.unanchored, pCommand.UNKNOWN) // TODO Do anything here?
  
    /** PC read a scroll of remove curse */
    val REMOVE_CURSE: Event = Val(
      """you feel as though someone is watching over you""".r.unanchored,
      pCommand.Read(Scroll(ScrollPower.REMOVE_CURSE)))
    
    val anyEvent: Regex = values.map(_.message.regex).mkString("|").r
    
    val events: Regex = s"($anyEvent\\s*)*".r */


  /** Given the top line of a screen from Rogue, return the corresponding [[Event]] if there is one; otherwise
   * returns an error message */
  def interpretMessage(messageLine: String): Either[String, Seq[Event]] = RogueParsers.parseAll[Seq[Event]](RogueParsers.events, messageLine) match {
    case RogueParsers.Success(events: Seq[Event], _) => Right(events)
    case RogueParsers.Failure(msg, _) => Left(s"Could not parse message line $messageLine: $msg")
    case RogueParsers.Error(err, _) => Left(err)
  }

  object PC_HIT extends Event

  case class HitBy(monsterType: MonsterType) extends Event

  object REMOVE_CURSE extends Event {
    override def inference: pCommand = pCommand.Read(Scroll(ScrollPower.REMOVE_CURSE))
  }

  case class PickedUp(slot: Slot, item: pItem) extends Event

}
