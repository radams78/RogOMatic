package gamedata

import domain.Domain
import domain.Domain._

/** The set of slots in the PC's inventory */
case class Slot(label: Char) extends Ordered[Slot] {
  override def compare(that: Slot): Int = label.compareTo(that.label)

  override def toString: String = label.toString
}

object Slot {
  val A: Slot = Slot('a')
  val B: Slot = Slot('b')
  val C: Slot = Slot('c')
  val D: Slot = Slot('d')
  val E: Slot = Slot('e')
  val F: Slot = Slot('f')
  val G: Slot = Slot('g')
  val H: Slot = Slot('h')
  val I: Slot = Slot('i')

  def parse(slot: String): Either[String, Slot] =
    if (slot.length == 1 && slot.head.isLower) Right(Slot(slot.head)) else Left(s"Unrecognised inventory slot: $slot")

  implicit def domain: Domain[Slot] = Domain.flatDomain
}

/** Partial information about a slot: either a slot, or UNKNOWN */
case class pSlot(slot: Option[Slot]) {
  def keypress: Either[String, Char] = slot match {
    case Some(s) => Right(s.label)
    case None => Left(s"Error: Unknown slot")
  }
}

object pSlot {
  val UNKNOWN: pSlot = pSlot(None)

  def apply(slot: Slot): pSlot = pSlot(Some(slot))

  implicit def toPSlot(x: Slot): pSlot = pSlot(Some(x))

  implicit def domain: Domain[pSlot] = (x: pSlot, y: pSlot) => for (slot <- x.slot.merge(y.slot)) yield pSlot(slot)
}