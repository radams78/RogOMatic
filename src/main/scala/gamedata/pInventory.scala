package gamedata

import domain.Domain._
import domain.{Domain, pOption}
import gamedata.fact.{Fact, ProvidesKnowledge, UsesKnowledge}
import gamedata.item.{InSlot, pItem}

/** Partial information about the PC's inventory */
case class pInventory(private val items: Map[Slot, pOption[pItem]],
                      wearing: pOption[Slot],
                      wielding: pOption[Slot]) {
  override def toString: String = {
    (for ((slot, item) <- items) yield s"$slot) $item").mkString("\n") +
      (wielding match {
        case pOption.UNKNOWN => "Wielding: UNKNOWN\n"
        case pOption.NONE => ""
        case pOption.Some(weapon) => s"Wielding: $weapon\n"
      }) +
      (wearing match {
        case pOption.UNKNOWN => "Armor: UNKNOWN"
        case pOption.NONE => ""
        case pOption.Some(armor) => s"Armor: $armor"
      })
  }
}

object pInventory {
  def apply(): pInventory = new pInventory(Map(), pOption.UNKNOWN, pOption.UNKNOWN)

  def apply(items: Map[Slot, pItem], wearing: Option[Slot], wielding: Option[Slot]): pInventory =
    new pInventory(
      items.view.mapValues((item: pItem) => pOption.Some(item)).toMap ++
        (for (s <- Slot.ALL if !items.contains(s)) yield s -> pOption.NONE).toMap,
      pOption.Known(wearing),
      pOption.Known(wielding)
    )

  implicit def domain: Domain[pInventory] = (x: pInventory, y: pInventory) => for {
    items <- x.items.merge(y.items)
    wearing <- x.wearing.merge(y.wearing)
    wielding <- x.wielding.merge(y.wielding)
  } yield new pInventory(items, wearing, wielding)

  implicit def providesKnowledge: ProvidesKnowledge[pInventory] = (self: pInventory) => {
    self.items.flatMap({ case (s: Slot, oi: pOption[item.pItem]) =>
      oi match {
        case pOption.UNKNOWN => Set()
        case pOption.NONE => Set(InSlot(s, None))
        case pOption.Some(i) => i.implications + item.InSlot(s, Some(i))
      }
    }).toSet
  }

  implicit def usesKnowledge: UsesKnowledge[pInventory] = (self: pInventory, fact: Fact) => {
    val _items: Either[String, Map[Slot, pOption[pItem]]] =
      self.items.foldLeft[Either[String, Map[Slot, pOption[pItem]]]](
        Right(Map())
      )({
        case (Left(err), _) => Left(err)
        case (Right(_items), (slot, pOption.Some(i))) => for (j <- i.infer(fact)) yield _items + (slot -> pOption.Some(j))
        case (Right(_items), (slot, i)) => Right(_items + (slot -> i))
      })
    for (__items <- _items) yield new pInventory(__items, self.wearing, self.wielding)
  }
}

