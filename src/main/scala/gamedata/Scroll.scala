package gamedata

import gamedata.Domain._
import gamedata.ScrollPower.ScrollPower

/** A stack of scrolls 
 *
 * Invariant:
 * - scroll.infer(scroll.scrollKnowledge) == Right(scroll) */
case class Scroll(quantity: Option[Int], title: Option[String], power: Option[ScrollPower]) extends Item {
  def scrollKnowledge: ScrollKnowledge = (title, power) match {
    case (Some(t), Some(p)) => ScrollKnowledge(Map(t -> p))
    case _ => ScrollKnowledge()
  }

  override def toString: String =
    (Seq("a scroll") ++
      (for (p <- power) yield s"of $p") ++
      (for (t <- title) yield s"entitled: '$t'")).mkString(" ")

  /** Infer what we can about this scroll from the fact that title is mapped to power */
  def infer(_title: String, _power: ScrollPower): Either[String, Scroll] = for {
    inferredTitle <- if (power contains _power) title.merge(Some(_title)) else Right(title)
    inferredPower <- if (title contains _title) power.merge(Some(_power)) else Right(power)
  } yield Scroll(quantity, inferredTitle, inferredPower)

  def infer(scrollKnowledge: ScrollKnowledge): Either[String, Scroll] = (title, power) match {
    case (Some(t), power) => for {p <- power.merge(scrollKnowledge.getPower(t))} yield Scroll(quantity, title, p)
    case (None, Some(p)) => Right(Scroll(quantity, scrollKnowledge.getTitle(p), Some(p)))
    case _ => Right(this)
  }

  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Scroll(thatQuantity, thatTitle, thatPower) => for {
      inferredQuantity <- quantity.merge(thatQuantity)
      inferredTitle <- title.merge(thatTitle)
      inferredPower <- power.merge(thatPower)
    } yield Scroll(inferredQuantity, inferredTitle, inferredPower).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }
}

object Scroll {
  def apply(power: ScrollPower): Scroll = Scroll(None, None, Some(power))

  def apply(quantity: Int, title: String): Scroll = Scroll(Some(quantity), Some(title), None)

  implicit def scrollDomain: Domain[Scroll] = (x: Scroll, y: Scroll) => for {quantity <- x.quantity.merge(y.quantity)
                                                                             title <- x.title.merge(y.title)
                                                                             power <- x.power.merge(y.power)} yield Scroll(quantity, title, power)
}