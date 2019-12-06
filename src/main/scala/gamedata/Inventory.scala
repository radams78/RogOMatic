package gamedata

import scala.util.matching.UnanchoredRegex

/** The PC's inventory */
case class Inventory(items: Map[Slot, Item] = Map(),
                     wearing: Option[Slot] = None,
                     wielding: Option[Slot] = None)

object Inventory {
  private val wearingRegex: UnanchoredRegex = """(\w)\) (.*) being worn""".r.unanchored
  private val wieldingRegex: UnanchoredRegex = """(\w)\) (.*) in hand""".r.unanchored
  private val inventoryLineRegex: UnanchoredRegex = """(\w)\) (.*?)\s*$""".r.unanchored

  /** Given a screen retrieved from Rogue displaying the inventory, return the corresponding [[Inventory]] */
  def parseInventoryScreen(screen: String): Option[Inventory] = {
    val lines: Array[String] = screen
      .split("\n")
      .takeWhile((s: String) => !s.contains("--press space to continue--"))
    val items: Option[Map[Slot, Item]] = lines.foldLeft[Option[Seq[(Slot, Item)]]](Some(Seq()))({
      case (None, _) => None
      case (Some(l), wearingRegex(slot, armor)) =>
        for (i <- Item.parse(armor)) yield l :+ (Slot.parse(slot), i)
      case (Some(l), wieldingRegex(slot, weapon)) =>
        for (i <- Item.parse(weapon)) yield l :+ (Slot.parse(slot), i)
      case (Some(l), inventoryLineRegex(slot, item)) =>
        for (i <- Item.parse(item)) yield l :+ (Slot.parse(slot), i)
    }).map(_.toMap)

    for (ii <- items) yield
      Inventory(
        ii,
        lines.collectFirst({ case wearingRegex(slot, _) => Slot.parse(slot) }),
        lines.collectFirst({ case wieldingRegex(slot, _) => Slot.parse(slot) })
      )
  }
}
