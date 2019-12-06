package gamedata

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

  def parse(slot: String): Slot = Slot(slot.head)
}