package gamedata

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.item.pItem

import scala.util.matching.UnanchoredRegex


/** Partial information about the PC's inventory 
 *
 * @param items - Map giving the contents of each inventory slot
 *              If item(s) is:
 *  - None then slot s is empty
 *  - Some(i) then slot s contains i
 *  - If ! item.keys.contains(s) then it is unknown whether slot s is empty or not */
case class pInventory(items: Map[Slot, Option[pItem]],
                      wearing: pLift[Option[Slot]],
                      wielding: pLift[Option[Slot]]) {
  override def toString: String = {
    (for ((slot, item) <- items.toList.sortBy(_._1)) yield s"$slot) $item\n") +
      (wielding match {
        case pLift.UNKNOWN => "pWeapon: UNKNOWN\n"
        case pLift.Known(None) => ""
        case pLift.Known(Some(weapon)) => s"pWeapon: $weapon\n"
      }) +
      (wearing match {
        case pLift.UNKNOWN => "Armor: UNKNOWN"
        case pLift.Known(None) => ""
        case pLift.Known(Some(armor)) => s"Armor: $armor"
      })
  }
}

object pInventory {
  def apply(): pInventory = new pInventory(Map(), pLift.UNKNOWN, pLift.UNKNOWN)

  def apply(items: Map[Slot, pItem], wearing: Option[Slot], wielding: Option[Slot]): pInventory =
    new pInventory(
      items.view.mapValues(Some(_)).toMap ++ (for (s <- Slot.ALL if !items.contains(s)) yield s -> None),
      pLift.Known(wearing),
      pLift.Known(wielding)
    )

  /** Given a screen retrieved from Rogue displaying the inventory, return the corresponding [[pInventory]] */
  def parseInventoryScreen(screen: String): Either[String, pInventory] = {
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
      new pInventory(
        (for (slot <- Slot.ALL) yield slot -> ii.get(slot)).toMap,
        pLift.Known(lines.collectFirst({ case wearingRegex(slot, _) => Slot.parse(slot) match {
          case Left(err) => return Left(err)
          case Right(s) => s
        }
        })),
        pLift.Known(lines.collectFirst({ case wieldingRegex(slot, _) => Slot.parse(slot) match {
          case Left(err) => return Left(err)
          case Right(s) => s
        }
        }))
      )
  }

  implicit def domain: Domain[pInventory] = (x: pInventory, y: pInventory) => for {
    items <- x.items.merge(y.items)
    wearing <- x.wearing.merge(y.wearing)
    wielding <- x.wielding.merge(y.wielding)
  } yield new pInventory(items, wearing, wielding)

  implicit def providesKnowledge: ProvidesKnowledge[pInventory] = (self: pInventory) => {
    self.items.flatMap({ case (s: Slot, oi: Option[item.pItem]) =>
      oi.toSet.flatMap((i: item.pItem) => i.implications + InSlot(s, i))
    }).toSet
  }

  implicit def usesKnowledge: UsesKnowledge[pInventory] = (self: pInventory, fact: Fact) => {
    val _items: Either[String, Map[Slot, Option[pItem]]] = self.items.foldLeft[Either[String, Map[Slot, Option[pItem]]]](
      Right(Map())
    )({
      case (Left(err), _) => Left(err)
      case (Right(_items), (slot, None)) => Right(_items + (slot -> None))
      case (Right(_items), (slot, Some(i))) => for (j <- i.infer(fact)) yield _items + (slot -> Some(j))
    })
    for (__items <- _items) yield pInventory(__items, self.wearing, self.wielding)
  }

  private val wearingRegex: UnanchoredRegex = """(\w)\) (.*) being worn""".r.unanchored
  private val wieldingRegex: UnanchoredRegex = """(\w)\) (.*) in hand""".r.unanchored
  private val inventoryLineRegex: UnanchoredRegex = """(\w)\) (.*?)\s*$""".r.unanchored
}

case class InSlot(slot: Slot, item: pItem) extends Fact