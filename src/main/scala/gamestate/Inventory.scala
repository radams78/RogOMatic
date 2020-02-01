package gamestate

import domain.pLift
import gamedata.ProvidesKnowledge._
import gamedata._
import gamedata.fact.Fact
import gamedata.item.pItem

import scala.util.matching.UnanchoredRegex

/** An inventory in which we know whether every slot is empty or full.
 *
 * For an inventory in which the status of some slots is unknown, use [[pInventory]] */
case class Inventory(private val items: Map[Slot, pItem], wearing: Option[Slot], wielding: Option[Slot]) {
  /** Contents of the given slot */
  def item(slot: Slot): Option[pItem] = items.get(slot)

  def topInventory: pInventory = new pInventory(
    (for (slot <- Slot.ALL) yield slot -> pLift.Known(item(slot))).toMap,
    pLift.Known(wearing),
    pLift.Known(wielding)
  )
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
    })) yield Inventory(items, self.wielding, self.wearing)
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