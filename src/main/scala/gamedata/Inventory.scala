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

  Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> Weapon(WeaponType.MACE, +1, +1),
      Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> Weapon(31, WeaponType.ARROW, +0, +0)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )
}
