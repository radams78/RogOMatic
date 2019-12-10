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
  def parseInventoryScreen(screen: String): Either[String, Inventory] = {
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
      Inventory(
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
}
