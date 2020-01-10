package gamedata

import domain.Domain
import domain.Domain._
import gamedata.items.Item
import gamestate.ScrollKnowledge

import scala.util.matching.UnanchoredRegex


/** Partial information about the PC's inventory 
 *
 * @param items - Map giving the contents of each inventory slot
 *              If items(s) is:
 *  - None then slot s is empty
 *  - Some(i) then slot s contains i
 *  - If ! items.keys.contains(s) then it is unknown whether slot s is empty or not */
case class pInventory(items: Map[Slot, Option[Item]],
                      wearing: pOption[Slot],
                      wielding: pOption[Slot]) {
  def infer(item: ProvidesScrollKnowledge): Either[String, pInventory] = infer(item.scrollKnowledge)

  def infer(scrollKnowledge: ScrollKnowledge): Either[String, pInventory] = {
    def step2(x: Option[Item]): Either[String, Option[Item]] = x match {
      case None => Right(None)
      case Some(y) => for (z <- y.infer(scrollKnowledge)) yield Some(z)
    }

    def step3(items: Map[Slot, Option[Item]]): Either[String, Map[Slot, Option[Item]]] = items.foldLeft[Either[String, Map[Slot, Option[Item]]]](
      Right(Map())
    )({
      case (e: Either[String, Map[Slot, Option[Item]]], (s: Slot, oi: Option[Item])) =>
        for {
          m <- e
          z <- step2(oi)
        } yield m.updated(s, z)
    })

    for (ii <- step3(items)) yield new pInventory(ii, wearing, wielding)
  }
}

object pInventory {
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