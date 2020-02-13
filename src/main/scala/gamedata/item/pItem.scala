package gamedata.item

import domain.{Domain, pLift}
import gamedata.fact.{Fact, ProvidesKnowledge}
import rogue.RogueParsers

/** An item that the PC can pick up 
 *
 * Contract:
 * - _implications is monotone
 * - x <= x.infer(fact)
 * - if x.impliciations contains fact then x.infer(fact) == x */
trait pItem {
  def consumeOne: pLift[Option[pItem]]

  def infer(fact: Fact): Either[String, pItem] = Right(this)

  def merge(that: pItem): Either[String, pItem]

  def _implications: Set[Fact] = Set()
}

object pItem {

  implicit def providesKnowledge: ProvidesKnowledge[pItem] = (self: pItem) => self._implications

  /** Unknown item
   *
   * Contract: for any item x, 
   * x.merge(UNKNOWN) == x
   * UNKNOWN.merge(x) == x */
  case object UNKNOWN extends pItem {
    override def merge(that: pItem): Either[String, pItem] = Right(that)

    override def _implications: Set[Fact] = Set()

    override def consumeOne: pLift[Option[pItem]] = pLift.UNKNOWN
  }

  def parse(description: String): Either[String, pItem] = RogueParsers.parseAll(RogueParsers.pItem, description) match {
    case RogueParsers.Success(item: pItem, _) => Right(item)
    case RogueParsers.Failure(msg, _) => Left(s"Could not parse $description: $msg")
    case RogueParsers.Error(err, _) => Left(s"Error when parsing $description: $err")
  }

  implicit def domain: Domain[pItem] = (x: pItem, y: pItem) => x.merge(y)
}
