package gamedata

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.item.magic.potion.Potion
import gamedata.item.magic.potion.Potion._
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.scroll.Scroll.Scroll
import gamedata.item.{InSlot, pItem}

/** Partial information about a move that can be made by the player in Rogue.
 *
 * Invariants:
 * - implications is monotone
 * - command <= command.infer(fact)
 * - command.infer(command.implications) == command
 * - command.infer(fact).implications contains fact */
sealed trait pCommand {
  /** True if the command consumes one unit of the stack of items in slot. */
  def consumes(slot: Slot): Boolean = false

  /** Facts that are known to be true *after* the command has been performed */
  def implications: Set[Fact] = Set()

  /** Combine two pieces of information about a command */
  def merge(that: pCommand): Either[String, pCommand]

  def _infer(fact: Fact): Either[String, pCommand] = Right(this)
}

object pCommand {

  case object UNKNOWN extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = Right(that)
  }


  /** Drink a potion */
  case class Quaff(slot: pLift[Slot], potion: Potion) extends pCommand {
    override def consumes(_slot: Slot): Boolean = slot == pLift.Known(_slot)

    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Quaff(thatSlot, thatPotion) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredPotion <- potion.merge(thatPotion)
      } yield Quaff(inferredSlot, inferredPotion)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = potion.implications.++((slot, potion.consumeOne) match {
      case (pLift.Known(s), pLift.Known(i)) => Set(InSlot(s, i))
      case _ => Set()
    })

    override def _infer(fact: Fact): Either[String, Quaff] = fact match {
      case InSlot(s, Some(i)) =>
        if (slot == pLift.Known(s))
          for (p <- potion.merge(i))
            yield new Quaff(slot, p)
        else Right(this)
      case InSlot(s, None) if slot == pLift.Known(s) => Left(s"Slot $s contains $potion and is empty")
      case f =>
        for (p <- potion.infer(f))
          yield new Quaff(slot, p)
    }
  }

  object Quaff {
    def apply(potion: Potion): Quaff = Quaff(pLift.UNKNOWN, potion)

    def apply(slot: Slot): Quaff = Quaff(pLift.Known(slot), Potion.UNKNOWN)

    def apply(slot: Slot, potion: Potion): Quaff = Quaff(pLift.Known(slot), potion)
  }

  /** Read a scroll */
  case class Read(slot: pLift[Slot], scroll: Scroll) extends pCommand {
    override def consumes(_slot: Slot): Boolean = slot == pLift.Known(_slot)

    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Read(thatSlot, thatScroll) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredScroll <- scroll.merge(thatScroll)
      } yield Read(inferredSlot, inferredScroll)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = scroll.implications.++(slot match {
      case pLift.UNKNOWN => Set()
      case pLift.Known(s) => scroll.consumeOne match {
        case pLift.UNKNOWN => Set()
        case pLift.Known(i) => Set(InSlot(s, i))
      } // TODO Duplication
    })

    override def _infer(fact: Fact): Either[String, Read] = fact match {
      case InSlot(s, Some(i)) =>
        if (slot == pLift.Known(s))
          for (s <- scroll.merge(i))
            yield new Read(slot, s)
        else Right(this)
      case InSlot(s, None) if slot == pLift.Known(s) => Left(s"Slot $s contains $scroll and is empty")
      case f =>
        for (s <- scroll.infer(f))
          yield new Read(slot, s)
    }
  }

  object Read {
    def apply(slot: Slot, scroll: Scroll): Read = Read(pLift.Known(slot), scroll)

    def apply(slot: Slot): Read = Read(pLift.Known(slot), Scroll.UNKNOWN)

    def apply(scroll: Scroll): Read = Read(pLift.UNKNOWN, scroll)
  }

  /** Throw an item */
  case class Throw(dir: Direction, slot: Slot, item: pItem) extends pCommand {
    override def consumes(_slot: Slot): Boolean = slot == _slot

    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Throw(thatDir, thatSlot, thatItem) => for {
        inferredDir <- dir.merge(thatDir)
        inferredSlot <- slot.merge(thatSlot)
        inferredItem <- item.merge(thatItem)
      } yield Throw(inferredDir, inferredSlot, inferredItem)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = item.implications.+(gamedata.item.InSlot(slot, Some(item)))

    override def _infer(fact: Fact): Either[String, pCommand] = fact match {
      case InSlot(s, Some(i)) =>
        if (slot == s)
          for (ii <- item.merge(i))
            yield new Throw(dir, slot, ii)
        else Right(this)
      case InSlot(s, None) if slot == s => Left(s"Slot $s contains $item and is empty")
      case f =>
        for (ii <- item.infer(f))
          yield new Throw(dir, slot, ii)
    }
  }

  object Throw {
    def apply(dir: Direction, slot: Slot): Throw = Throw(dir, slot, pItem.UNKNOWN)
  }

  /** Wield a weapon */
  case class Wield(slot: Slot) extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Wield(thatSlot) => for (inferredSlot <- slot.merge(thatSlot)) yield Wield(inferredSlot)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Rest */
  case object REST extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case REST => Right(REST)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up */
  case object UP extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case UP => Right(UP)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down */
  case object DOWN extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case DOWN => Right(DOWN)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move left */
  case object LEFT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case LEFT => Right(LEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move right */
  case object RIGHT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case RIGHT => Right(RIGHT)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up left */
  case object UPLEFT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case UPLEFT => Right(UPLEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up right */
  case object UPRIGHT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case UPRIGHT => Right(UPRIGHT)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down right */
  case object DOWNRIGHT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case DOWNRIGHT => Right(DOWNRIGHT)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Go downstairs */
  case object DOWNSTAIRS extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case DOWNSTAIRS => Right(DOWNSTAIRS)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down left */
  case object DOWNLEFT extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case DOWNLEFT => Right(DOWNLEFT)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  implicit def domain: Domain[pCommand] = (x: pCommand, y: pCommand) => x.merge(y)

  implicit def providesKnowledge: ProvidesKnowledge[pCommand] = (self: pCommand) => self.implications

  implicit def usesKnowledge: UsesKnowledge[pCommand] = (self: pCommand, fact: Fact) => self._infer(fact)
}
