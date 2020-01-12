package gamedata.items

import domain.Domain
import domain.Domain._
import gamedata.items.ScrollPower.ScrollPower
import gamedata.{Fact, UsesKnowledge}

/** A stack of scrolls 
 *
 * Invariant:
 * - scroll.infer(scroll.scrollKnowledge) == Right(scroll) */
case class Scroll(quantity: Option[Int], title: Option[String], power: Option[ScrollPower]) extends MagicItem[String, ScrollPower] {
  override def implications: Set[Fact] = (title, power) match {
    case (Some(t), Some(p)) => Set(Fact.ScrollKnowledge(t, p))
    case _ => Set()
  }

  override def toString: String =
    (Seq("a scroll") ++
      (for (p <- power) yield s"of $p") ++
      (for (t <- title) yield s"entitled: '$t'")).mkString(" ")


  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Scroll(thatQuantity, thatTitle, thatPower) => for {
      inferredQuantity <- quantity.merge(thatQuantity)
      inferredTitle <- title.merge(thatTitle)
      inferredPower <- power.merge(thatPower)
    } yield Scroll(inferredQuantity, inferredTitle, inferredPower).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }

  override def attribute: Option[String] = title
}

object Scroll {
  val UNKNOWN: Scroll = Scroll(None, None, None)

  def apply(power: ScrollPower): Scroll = Scroll(None, None, Some(power))

  def apply(quantity: Int, title: String): Scroll = Scroll(Some(quantity), Some(title), None)

  implicit def usesKnowledge: UsesKnowledge[Scroll] = (self: Scroll, fact: Fact) => (fact, self.title, self.power) match {
    case (Fact.MagicItemKnowledge(_t, _p), Some(t), Some(p)) if (t == _t && p != _p) || (t != _t && p == _p) =>
      Left(s"Incompatible information: $t -> $p and ${_t} -> ${_p}")
    case (Fact.MagicItemKnowledge(_t, _p: ScrollPower), Some(t), None) if t == _t => Right(Scroll(self.quantity, Some(t), Some(_p)))
    case (Fact.MagicItemKnowledge(_t: String, _p), None, Some(p)) if p == _p => Right(Scroll(self.quantity, Some(_t), Some(p)))
    case _ => Right(self)
  }

  implicit def scrollDomain: Domain[Scroll] = (x: Scroll, y: Scroll) => x.merge(y)

  def apply(title: String, power: ScrollPower): Scroll = Scroll(None, Some(title), Some(power))
}