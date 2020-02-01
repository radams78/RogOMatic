package gamedata.item

import domain.pLift
import gamedata.fact.Fact
import gamedata.{Slot, pCommand}

/** A fact that gives the contents of an inventory slot */
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
