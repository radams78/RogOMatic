package gamedata

import domain.Domain
import domain.Domain._
import gamedata.item.Item

import scala.util.matching.UnanchoredRegex


/** Partial information about the PC's inventory 
 *
 * @param items - Map giving the contents of each inventory slot
 *              If item(s) is:
 *  - None then slot s is empty
 *  - Some(i) then slot s contains i
 *  - If ! item.keys.contains(s) then it is unknown whether slot s is empty or not */
case class pInventory(items: Map[Slot, Option[Item]],
                      wearing: pOption[Slot],
                      wielding: pOption[Slot])

object pInventory {
  implicit def providesKnowledge: ProvidesKnowledge[pInventory] = (self: pInventory) => self.items.values.flatMap((oi: Option[Item]) => oi.toSet.flatMap((i: Item) => i.implications)).toSet

  implicit def usesKnowledge: UsesKnowledge[pInventory] = (self: pInventory, fact: Fact) => {
    val _items: Either[String, Map[Slot, Option[Item]]] = self.items.foldLeft[Either[String, Map[Slot, Option[Item]]]](
      Right(Map())
    )({
      case (Left(err), _) => Left(err)
      case (Right(_items), (slot, None)) => Right(_items + (slot -> None))
      case (Right(_items), (slot, Some(i))) => for (j <- i.infer(fact)) yield _items + (slot -> Some(j))
    })
    for (__items <- _items) yield pInventory(__items, self.wearing, self.wielding)
  }

  def apply(): pInventory = new pInventory(Map(), pOption.UNKNOWN, pOption.UNKNOWN)

  def apply(items: Map[Slot, Item], wearing: Option[Slot], wielding: Option[Slot]): pInventory =
    new pInventory(
      items.view.mapValues(Some(_)).toMap ++ (for (s <- Slot.ALL if !items.contains(s)) yield s -> None),
      pOption.known(wearing),
      pOption.known(wielding)
    )

  private val wearingRegex: UnanchoredRegex = """(\w)\) (.*) being worn""".r.unanchored
  private val wieldingRegex: UnanchoredRegex = """(\w)\) (.*) in hand""".r.unanchored
  private val inventoryLineRegex: UnanchoredRegex = """(\w)\) (.*?)\s*$""".r.unanchored

  /** Given a screen retrieved from Rogue displaying the inventory, return the corresponding [[pInventory]] */
  def parseInventoryScreen(screen: String): Either[String, pInventory] = {
    val lines: Array[String] = screen
      .split("\n")
      .takeWhile((s: String) => !s.contains("--press space to continue--"))
    val items: Either[String, Map[Slot, Item]] = lines.foldLeft[Either[String, Seq[(Slot, Item)]]](Right(Seq()))({
      case (Left(s), _) => Left(s)
      case (Right(l), wearingRegex(slot, armor)) => for {
        i <- Item.parse(armor)
        s <- Slot.parse(slot)
      } yield l :+ (s, i)
      case (Right(l), wieldingRegex(slot, weapon)) => for {
        s <- Slot.parse(slot)
        i <- Item.parse(weapon)
      } yield l :+ (s, i)
      case (Right(l), inventoryLineRegex(slot, item)) => for {
        s <- Slot.parse(slot)
        i <- Item.parse(item)
      } yield l :+ (s, i)
    }).map(_.toMap)

    for (ii <- items) yield
      new pInventory(
        (for (slot <- Slot.ALL) yield slot -> ii.get(slot)).toMap,
        pOption.known(lines.collectFirst({ case wearingRegex(slot, _) => Slot.parse(slot) match {
          case Left(err) => return Left(err)
          case Right(s) => s
        }
        })),
        pOption.known(lines.collectFirst({ case wieldingRegex(slot, _) => Slot.parse(slot) match {
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
}