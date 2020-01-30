package gamedata

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.item.pItem


/** Partial information about the PC's inventory */
case class pInventory(private val items: Map[Slot, pLift[Option[pItem]]],
                      wearing: pLift[Option[Slot]],
                      wielding: pLift[Option[Slot]]) {
  override def toString: String = {
    (for ((slot, item) <- items) yield s"$slot) $item").mkString("\n") +
      (wielding match {
        case pLift.UNKNOWN => "Wielding: UNKNOWN\n"
        case pLift.Known(None) => ""
        case pLift.Known(Some(weapon)) => s"Wielding: $weapon\n"
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
      items.view.mapValues((item: pItem) => pLift.Known(Some(item))).toMap ++
        (for (s <- Slot.ALL if !items.contains(s)) yield s -> pLift.Known(None)),
      pLift.Known(wearing),
      pLift.Known(wielding)
    )

  implicit def domain: Domain[pInventory] = (x: pInventory, y: pInventory) => for {
    items <- x.items.merge(y.items)
    wearing <- x.wearing.merge(y.wearing)
    wielding <- x.wielding.merge(y.wielding)
  } yield new pInventory(items, wearing, wielding)

  implicit def providesKnowledge: ProvidesKnowledge[pInventory] = (self: pInventory) => {
    self.items.flatMap({ case (s: Slot, oi: pLift[Option[item.pItem]]) =>
      oi match {
        case pLift.UNKNOWN => Set()
        case pLift.Known(None) => Set(InSlot(s, None))
        case pLift.Known(Some(i)) => i.implications + InSlot(s, Some(i))
      }
    }).toSet
  }

  implicit def usesKnowledge: UsesKnowledge[pInventory] = (self: pInventory, fact: Fact) => {
    val _items: Either[String, Map[Slot, pLift[Option[pItem]]]] = self.items.foldLeft[Either[String, Map[Slot, pLift[Option[pItem]]]]](
      Right(Map())
    )({
      case (Left(err), _) => Left(err)
      case (Right(_items), (slot, pLift.Known(Some(i)))) => for (j <- i.infer(fact)) yield _items + (slot -> pLift.Known(Some(j)))
      case (Right(_items), (slot, i)) => Right(_items + (slot -> i))
    })
    for (__items <- _items) yield new pInventory(__items, self.wearing, self.wielding)
  }
}

case class InSlot(slot: Slot, item: Option[pItem]) extends Fact {
  override def after(command: pCommand): Either[String, Set[Fact]] = if (command.consumes(slot)) {
    item match {
      case None => Left(s"Performed command $command while $slot is empty")
      case Some(i) => i.consumeOne match {
        case pLift.UNKNOWN => Right(Set())
        case pLift.Known(i) => Right(Set(InSlot(slot, i)))
      }
    }
  } else Right(Set(this))
}