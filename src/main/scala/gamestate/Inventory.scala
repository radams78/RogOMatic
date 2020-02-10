package gamestate

import domain.pOption
import gamedata._
import gamedata.fact.ProvidesKnowledge._
import gamedata.fact.{Fact, ProvidesKnowledge, UsesKnowledge}
import gamedata.item.armor.Armor
import gamedata.item.pItem
import gamedata.item.weapon.Wieldable

import scala.util.matching.UnanchoredRegex

/** An inventory in which we know whether every slot is empty or full.
 *
 * For an inventory in which the status of some slots is unknown, use [[pInventory]] */
case class Inventory(private val items: Map[Slot, pItem], wearingSlot: Option[Slot], wielding: Option[Slot]) {
  assert(wearingSlot.forall((slot: Slot) => items(slot).isInstanceOf[Armor]))
  assert(wielding.forall((slot: Slot) => items(slot).isInstanceOf[Wieldable]))

  def wearing: Option[Armor] = wearingSlot match {
    case None => None
    case Some(s) => items(s) match {
      case a: Armor => Some(a)
      case i => throw new Error(s"Item being worn is not armor: $i")
    }
  }

  /** Contents of the given slot */
  def item(slot: Slot): Option[pItem] = items.get(slot)

  def topInventory: pInventory = new pInventory(
    (for (slot <- Slot.ALL) yield slot -> pOption.Known(item(slot))).toMap,
    pOption.Known(wearingSlot),
    pOption.Known(wielding)
  )

  override def toString: String =
    (for (slot <- Slot.ALL) yield item(slot) match {
      case None => ""
      case Some(item) => s"$slot) $item\n"
    }).mkString("") +
      (wielding match {
        case None => "Wielding: none\n"
        case Some(weapon) => s"Wielding: $weapon) ${item(weapon).get}\n" // TODO
      }) +
      (wearing match {
        case None => "Wearing: none"
        case Some(armor) => s"Armor: $wearingSlot) $wearing" // TODO
      })

}

object Inventory {
  implicit def providesKnowledge: ProvidesKnowledge[Inventory] = (self: Inventory) => self.topInventory.implications

  implicit def usesKnowledge: UsesKnowledge[Inventory] = (self: Inventory, fact: Fact) => {
    for (items <- self.items.foldLeft[Either[String, Map[Slot, pItem]]](Right(Map()))({
      case (items: Either[String, Map[Slot, pItem]], (slot: Slot, item: pItem)) =>
        for {
          _items <- items
          _item <- item.infer(fact)
        } yield _items + (slot -> _item.asInstanceOf[pItem])
    })) yield Inventory(items, wearingSlot = self.wearingSlot, wielding = self.wielding)
  }

  /** Given a screen retrieved from Rogue displaying the inventory, return the corresponding [[pInventory]] */
  def parseInventoryScreen(screen: String): Either[String, Inventory] = {
    val lines: Array[String] = screen
      .split("\n")
      .takeWhile((s: String) => !s.contains("--press space to continue--"))
    val items: Either[String, Map[Slot, pItem]] = lines.foldLeft[Either[String, Seq[(Slot, pItem)]]](Right(Seq()))({
      case (Left(s), _) => Left(s)
      case (Right(l), wearingRegex(slot, armor)) => for {
        i <- pItem.parse(armor)
        s <- Slot.parse(slot)
      } yield l :+ (s, i)
      case (Right(l), wieldingRegex(slot, weapon)) => for {
        s <- Slot.parse(slot)
        i <- pItem.parse(weapon)
      } yield l :+ (s, i)
      case (Right(l), inventoryLineRegex(slot, item)) => for {
        s <- Slot.parse(slot)
        i <- pItem.parse(item)
      } yield l :+ (s, i)
    }).map(_.toMap)

    for (ii <- items) yield
      new Inventory(
        ii,
        lines.collectFirst({ case wearingRegex(slot, _) => Slot.parse(slot) match {
          case Left(err) => return Left(err)
          case Right(s) => s
        }
        }),
        lines.collectFirst({ case wieldingRegex(slot, _) => Slot.parse(slot) match {
          case Left(err) => return Left(err)
          case Right(s) => s
        }
        })
      )
  }

  private val wearingRegex: UnanchoredRegex = """(\w)\) (.*) being worn""".r.unanchored
  private val wieldingRegex: UnanchoredRegex = """(\w)\) (.*) in hand""".r.unanchored
  private val inventoryLineRegex: UnanchoredRegex = """(\w)\) (.*?)\s*$""".r.unanchored
}