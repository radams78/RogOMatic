package rogue

import domain.Domain
import domain.Domain._
import gamedata._
import gamedata.items.Potion._
import gamedata.items.Scroll.Scroll
import gamedata.items.{Item, Potion, Scroll}

/** Partial information about a move that can be made by the player in Rogue.
 *
 * Invariants:
 * - implications is monotone
 * - command <= command.infer(fact)
 * - command.infer(command.implications) == command
 * - command.infer(fact).implications contains fact */
// TODO Validation 
// TODO Prove invariants
sealed trait Command {
  /** Facts that can be deduced from this command */
  def implications: Set[Fact] = Set() // TODO

  /** Combine two pieces of information about a command */
  def merge(that: Command): Either[String, Command]

  /** Keypresses to send to Rogue to execute command */
  def keypresses: Either[String, Seq[Char]]

  private def infer(fact: Fact): Either[String, Command] = Right(this) // TODO
}

object Command {
  /** Drink a potion */
  case class Quaff(slot: pSlot, potion: Potion) extends Command {
    override def keypresses: Either[String, Seq[Char]] = for (k <- slot.keypress) yield Seq('q', k)

    override def merge(that: Command): Either[String, Command] = that match {
      case Quaff(thatSlot, thatPotion) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredPotion <- potion.merge(thatPotion)
      } yield Quaff(inferredSlot, inferredPotion)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = potion.implications // TODO Plus fact that potion is in slot
  }

  object Quaff {
    def apply(potion: Potion): Quaff = Quaff(pSlot.UNKNOWN, potion)

    // TODO Error handling
    def apply(inventory: pInventory, slot: Slot): Quaff = {
      Quaff(pSlot(slot), inventory.items(slot).asInstanceOf[Potion])
    }

    def apply(slot: Slot): Quaff = Quaff(pSlot(slot), Potion.UNKNOWN)
  }

  /** Read a scroll */
  case class Read(slot: pSlot, scroll: Scroll) extends Command {
    override def keypresses: Either[String, Seq[Char]] = for (k <- slot.keypress) yield Seq('r', k)

    override def merge(that: Command): Either[String, Command] = that match {
      case Read(thatSlot, thatScroll) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredScroll <- scroll.merge(thatScroll)
      } yield Read(inferredSlot, inferredScroll)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = scroll.implications
  }

  object Read {
    def apply(inventory: pInventory, slot: Slot): Read = Read(slot, inventory.items(slot).asInstanceOf[Scroll]) // TODO Better error handling

    def apply(slot: Slot, scroll: Scroll): Read = Read(pSlot(slot), scroll)

    def apply(slot: Slot): Read = Read(pSlot(slot), Scroll.UNKNOWN)

    def apply(scroll: Scroll): Read = Read(pSlot.UNKNOWN, scroll)
  }

  /** Throw an item */
  case class Throw(dir: Direction, slot: Slot, item: Item) extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('t', dir.keypress, slot.label))

    override def merge(that: Command): Either[String, Command] = that match {
      case Throw(thatDir, thatSlot, thatItem) => for {
        inferredDir <- dir.merge(thatDir)
        inferredSlot <- slot.merge(thatSlot)
        inferredItem <- item.merge(thatItem)
      } yield Throw(inferredDir, inferredSlot, inferredItem)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    override def implications: Set[Fact] = item.implications // TODO Plus fact that item is in slot
  }

  object Throw {
    def apply(dir: Direction, slot: Slot): Throw = Throw(dir, slot, Item.UNKNOWN)
  }

  /** Wield a weapon */
  case class Wield(slot: Slot) extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('w', slot.label))

    override def merge(that: Command): Either[String, Command] = that match {
      case Wield(thatSlot) => for (inferredSlot <- slot.merge(thatSlot)) yield Wield(inferredSlot)
      case _ => Left(s"Incompatible commands: $this and $that")
    }

    // TODO Implications
  }

  /** Rest */
  case object REST extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('.'))

    override def merge(that: Command): Either[String, Command] = that match {
      case REST => Right(REST)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up */
  case object UP extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('j'))

    override def merge(that: Command): Either[String, Command] = that match {
      case UP => Right(UP)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down */
  case object DOWN extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('k'))

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWN => Right(DOWN)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move left */
  case object LEFT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('h'))

    override def merge(that: Command): Either[String, Command] = that match {
      case LEFT => Right(LEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move right */
  case object RIGHT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('l'))

    override def merge(that: Command): Either[String, Command] = that match {
      case RIGHT => Right(RIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up left */
  case object UPLEFT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('y'))

    override def merge(that: Command): Either[String, Command] = that match {
      case UPLEFT => Right(UPLEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up right */
  case object UPRIGHT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('u'))

    override def merge(that: Command): Either[String, Command] = that match {
      case UPRIGHT => Right(UPRIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down right */
  case object DOWNRIGHT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('n'))

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNRIGHT => Right(DOWNRIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Go downstairs */
  case object DOWNSTAIRS extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('>'))

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNSTAIRS => Right(DOWNSTAIRS)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down left */
  case object DOWNLEFT extends Command {
    override val keypresses: Either[String, Seq[Char]] = Right(Seq('b'))

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNLEFT => Right(DOWNLEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  implicit def domain: Domain[Command] = (x: Command, y: Command) => x.merge(y)

  implicit def providesKnowledge: ProvidesKnowledge[Command] = (self: Command) => self.implications

  implicit def usesKnowledge: UsesKnowledge[Command] = (self: Command, fact: Fact) => self.infer(fact)
}
