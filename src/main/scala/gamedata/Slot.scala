package gamedata

/** The set of slots in the PC's inventory */
case class Slot(label: Char) extends Ordered[Slot] {
  override def compare(that: Slot): Int = label.compareTo(that.label)

  override def toString: String = label.toString
}

object Slot {
  val H: Slot = Slot('h')

  val A: Slot = Slot('a')
  val B: Slot = Slot('b')
  val C: Slot = Slot('c')
  val D: Slot = Slot('d')
  val E: Slot = Slot('e')
  val F: Slot = Slot('f')
  val G: Slot = Slot('g')

  def parse(slot: String): Either[String, Slot] =
    if (slot.length == 1 && slot.head.isLower) Right(Slot(slot.head)) else Left(s"Unrecognised inventory slot: $slot")
}