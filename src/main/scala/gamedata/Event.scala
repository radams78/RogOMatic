package gamedata

import gamedata.MonsterType.MonsterType
import gamedata.fact.ProvidesKnowledge
import gamedata.fact.ProvidesKnowledge._
import gamedata.item.magic.potion.{Potion, PotionPower}
import gamedata.item.magic.scroll.{Scroll, ScrollPower}

import scala.language.{implicitConversions, postfixOps}
import scala.util.matching.Regex
import scala.util.parsing.combinator._


/** An event indicated by a message in the message line */
sealed trait Event {
  def inference: pCommand = pCommand.UNKNOWN
}

case class Gold(value: Int) extends Event

case object HEALING extends Event {
  override def inference: pCommand = pCommand.Quaff(Potion(PotionPower.HEALING))
}

case class MissedBy(monsterType: MonsterType) extends Event

object Event extends RegexParsers with Parsers {
  private val goldRegex: Regex = """(\d+) pieces of gold""".r
  private val gold: Parser[Event] = Parser((msg: Input) => {
    val source: CharSequence = msg.source
    val offset: Int = msg.offset
    val start: Int = handleWhiteSpace(source, offset)
    goldRegex.findPrefixMatchOf(source.subSequence(start, source.length())) match {
      case Some(matched) => try {
        Success(Gold(matched.group(1).toInt), msg.drop(start + matched.end - offset))
      } catch {
        case e: NumberFormatException => Error(s"Could not parse quantity of gold in $source: $e", msg)
      }
      case None =>
        val found: String = if (start == source.length()) "end of source" else "'" + source.charAt(start) + "'"
        Failure("string matching regex '" + goldRegex + "' expected but " + found + " found", msg.drop(start - offset))
    }
  })
  private val healing: Parser[Event] = """you begin to feel better""".r ^^ { _: String => HEALING }
  private val removeCurse: Parser[Event] = "you feel as though someone is watching over you" ^^ { _: String => REMOVE_CURSE }
  private val missedByRegex: Regex = """the (.*) misses""".r
  private val missedBy: Parser[Event] = Parser((msg: Input) => {
    val source: CharSequence = msg.source
    val offset: Int = msg.offset
    val start: Int = handleWhiteSpace(source, offset)
    missedByRegex.findPrefixMatchOf(source.subSequence(start, source.length())) match {
      case Some(matched) => MonsterType.parse(matched.group(1)) match {
        case Left(err) => Error(err, msg)
        case Right(monsterType) => Success(MissedBy(monsterType),
          msg.drop(start + matched.end - offset))
      }
      case None =>
        val found: String = if (start == source.length()) "end of source" else "'" + source.charAt(start) + "'"
        Failure("string matching regex '" + missedByRegex + "' expected but " + found + " found", msg.drop(start - offset))
    }
  })
  private val youHit: Parser[Event] = "you hit" ^^ { _: String => PC_HIT }
  private val hitByRegex: Regex = """the (.*) hit""".r
  private val hitBy: Parser[Event] = Parser((msg: Input) => {
    val source: CharSequence = msg.source
    val offset: Int = msg.offset
    val start: Int = handleWhiteSpace(source, offset)
    hitByRegex.findPrefixMatchOf(source.subSequence(start, source.length())) match {
      case Some(matched) => MonsterType.parse(matched.group(1)) match {
        case Left(err) => Error(err, msg)
        case Right(monsterType) => Success(HitBy(monsterType),
          msg.drop(start + matched.end - offset))
      }
      case None =>
        val found: String = if (start == source.length()) "end of source" else "'" + source.charAt(start) + "'"
        Failure("string matching regex '" + hitByRegex + "' expected but " + found + " found", msg.drop(start - offset))
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

  private val message: Parser[Event] = gold | healing | missedBy | youHit | hitBy | removeCurse

  private val messages: Parser[Seq[Event]] = (message <~ (whiteSpace ?)) *

  /** Given the top line of a screen from Rogue, return the corresponding [[Event]] if there is one; otherwise
   * returns an error message */
  def interpretMessage(messageLine: String): Either[String, Seq[Event]] = parseAll(messages, messageLine) match {
    case Success(events, _) => Right(events)
    case Failure(msg, _) => Left(s"Could not parse message line $messageLine: $msg")
    case Error(err, _) => Left(err)
  }

  object PC_HIT extends Event

  case class HitBy(monsterType: MonsterType) extends Event

  object REMOVE_CURSE extends Event {
    override def inference: pCommand = pCommand.Read(Scroll(ScrollPower.REMOVE_CURSE))
  }

}
