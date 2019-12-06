package gamedata

import scala.util.matching.UnanchoredRegex

/** The PC's inventory */
case class Inventory(items: Map[Slot, Item] = Map(),
                     wearing: Option[Slot] = None,
                     wielding: Option[Slot] = None)

object Inventory {
  private val wearingRegex: UnanchoredRegex = """(\w)\) (.*) being worn""".r.unanchored
  private val wieldingRegex: UnanchoredRegex = """(\w)\) (.*) in hand""".r.unanchored
  private val inventoryLineRegex: UnanchoredRegex = """(\w)\) (.*)""".r.unanchored

  /** Given a screen retrieved from Rogue displaying the inventory, return the corresponding [[Inventory]] */
  def parseInventoryScreen(screen: String): Inventory = {
    val lines: Array[String] = screen
      .split("\n")
      .takeWhile((s: String) => !s.contains("--press space to continue--"))
    val items: Map[Slot, Item] = lines.map({
      case wearingRegex(slot, armor) => (Slot.parse(slot), Item.parse(armor))
      case wieldingRegex(slot, weapon) => (Slot.parse(slot), Item.parse(weapon))
      case inventoryLineRegex(slot, item) => (Slot.parse(slot), Item.parse(item))
    }).toMap
    Inventory(
      items,
      lines.collectFirst({ case wearingRegex(slot, _) => Slot.parse(slot) }),
      lines.collectFirst({ case wieldingRegex(slot, _) => Slot.parse(slot) })
    )
  }
}
