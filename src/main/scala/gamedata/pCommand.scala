package gamedata

import domain.Domain
import domain.Domain._
import gamedata.item.magic.potion.Potion
import gamedata.item.magic.potion.Potion._
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.scroll.Scroll.Scroll
import gamedata.item.pItem

/** Partial information about a move that can be made by the player in Rogue.
 *
 * Invariants:
 * - implications is monotone
 * - command <= command.infer(fact)
 * - command.infer(command.implications) == command
 * - command.infer(fact).implications contains fact */
sealed trait pCommand {
  def consumes(slot: Slot): Boolean = false

  /** Facts that can be deduced from this command */
  def implications: Set[Fact] = Set() // TODO Before or after?

  /** Combine two pieces of information about a command */
  def merge(that: pCommand): Either[String, pCommand]

  def _infer(fact: Fact): Either[String, pCommand] = Right(this)
}

object pCommand {

  case object UNKNOWN extends pCommand {
    override def merge(that: pCommand): Either[String, pCommand] = Right(that)
  }


  /** Drink a potion */
  case class Quaff(slot: pSlot, potion: Potion) extends pCommand {
    override def consumes(_slot: Slot): Boolean = slot == pSlot(_slot)

    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Quaff(thatSlot, thatPotion) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredPotion <- potion.merge(thatPotion)
      } yield Quaff(inferredSlot, inferredPotion)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = potion.implications.++(slot match {
      case pSlot(None) => Set()
      case pSlot(Some(s)) => Set(InSlot(s, Some(potion)))
    })

    override def _infer(fact: Fact): Either[String, Quaff] = fact match {
      case InSlot(s, Some(i)) =>
        if (slot == pSlot(s))
          for (p <- potion.merge(i))
            yield new Quaff(slot, p)
        else Right(this)
      case InSlot(s, None) if slot == pSlot(s) => Left(s"Slot $s contains $potion and is empty")
      case f =>
        for (p <- potion.infer(f))
          yield new Quaff(slot, p)
    }
  }

  object Quaff {
    def apply(potion: Potion): Quaff = Quaff(pSlot.UNKNOWN, potion)

    def apply(slot: Slot): Quaff = Quaff(pSlot(slot), Potion.UNKNOWN)
  }

  /** Read a scroll */
  case class Read(slot: pSlot, scroll: Scroll) extends pCommand {
    override def consumes(_slot: Slot): Boolean = slot == pSlot(_slot)

    override def merge(that: pCommand): Either[String, pCommand] = that match {
      case Read(thatSlot, thatScroll) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredScroll <- scroll.merge(thatScroll)
      } yield Read(inferredSlot, inferredScroll)
      case UNKNOWN => Right(this)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = scroll.implications.++(slot match {
      case pSlot(None) => Set()
      case pSlot(Some(s)) => Set(InSlot(s, Some(scroll)))
    })

    override def _infer(fact: Fact): Either[String, Read] = fact match {
      case InSlot(s, Some(i)) =>
        if (slot == pSlot(s))
          for (s <- scroll.merge(i))
            yield new Read(slot, s)
        else Right(this)
      case InSlot(s, None) if slot == pSlot(s) => Left(s"Slot $s contains $scroll and is empty")
      case f =>
        for (s <- scroll.infer(f))
          yield new Read(slot, s)
    }
  }

  object Read {
    def apply(inventory: pInventory, slot: Slot): Read = inventory.items(slot) match {
      case Some(scroll: Scroll) => Read(slot, scroll)
      case Some(item) => throw new Error(s"Tried to read $item")
      case None => throw new Error(s"Tried to read empty slot $slot")
    }

    def apply(slot: Slot, scroll: Scroll): Read = Read(pSlot(slot), scroll)

    def apply(slot: Slot): Read = Read(pSlot(slot), Scroll.UNKNOWN)

    def apply(scroll: Scroll): Read = Read(pSlot.UNKNOWN, scroll)
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

    override def implications: Set[Fact] = item.implications.+(InSlot(slot, Some(item)))

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
