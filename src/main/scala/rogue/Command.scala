package rogue

import gamedata.Domain._
import gamedata._

/** Partial information about a move that can be made by the player in Rogue. */
// TODO Validation 
sealed trait Command {
  /** Combine two pieces of information about a command */
  def merge(that: Command): Either[String, Command]

  /** Add information from the given knowledge about scroll powers */
  def infer(scrollKnowledge: ScrollKnowledge): Either[String, Command] = Right(this)

  /** Add information from the given knowledge about potion powers */
  def infer(potionKnowledge: PotionKnowledge): Either[String, Command] = Right(this)

  /** Keypresses to send to Rogue to execute command */
  def keypresses: Seq[Char]
}

object Command {

  /** Drink a potion */
  case class Quaff(slot: Option[Slot], potion: Potion) extends Command {
    override def keypresses: Seq[Char] = slot match {
      case Some(slot) => Seq('q', slot.label)
      case None => throw new Error("Tried to execute Quaff command with unknown slot") // TODO Duplication
    }

    override def infer(potionKnowledge: PotionKnowledge): Either[String, Command] =
      for (_potion <- potion.infer(potionKnowledge)) yield Quaff(slot, _potion)

    override def merge(that: Command): Either[String, Command] = that match {
      case Quaff(thatSlot, thatPotion) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredPotion <- potion.merge(thatPotion)
      } yield Quaff(inferredSlot, inferredPotion)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Read a scroll */
  case class Read(slot: Option[Slot], scroll: Scroll) extends Command {
    override def keypresses: Seq[Char] = slot match {
      case Some(slot) => Seq('r', slot.label)
      case None => throw new Error("Tried to execute Read command with unknown slot")
    }

    override def infer(scrollKnowledge: ScrollKnowledge): Either[String, Read] =
      for (_scroll <- scroll.infer(scrollKnowledge)) yield Read(slot, _scroll)

    override def merge(that: Command): Either[String, Command] = that match {
      case Read(thatSlot, thatScroll) => for {
        inferredSlot <- slot.merge(thatSlot)
        inferredScroll <- scroll.merge(thatScroll)
      } yield Read(inferredSlot, inferredScroll)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  object Read {
    def apply(scroll: Scroll): Read = Read(None, scroll)

    def apply(slot: Slot, scroll: Scroll): Read = Read(Some(slot), scroll)
  }

  /** Throw an item */
  case class Throw(dir: Direction, slot: Slot, item: Item) extends Command {
    override val keypresses: Seq[Char] = Seq('t', dir.keypress, slot.label)

    override def infer(scrollKnowledge: ScrollKnowledge): Either[String, Command] = item match {
      case scroll: Scroll => for (_scroll <- scroll.infer(scrollKnowledge)) yield Throw(dir, slot, _scroll)
      case _ => Right(this)
    }

    override def infer(potionKnowledge: PotionKnowledge): Either[String, Command] = item match {
      case potion: Potion => for (_potion <- potion.infer(potionKnowledge)) yield Throw(dir, slot, _potion)
      case _ => Right(this)
    }

    override def merge(that: Command): Either[String, Command] = that match {
      case Throw(thatDir, thatSlot, thatItem) => for {
        inferredDir <- dir.merge(thatDir)
        inferredSlot <- slot.merge(thatSlot)
        inferredItem <- item.merge(thatItem)
      } yield Throw(inferredDir, inferredSlot, inferredItem)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Rest */
  case object REST extends Command {
    override val keypresses: Seq[Char] = Seq('.')

    override def merge(that: Command): Either[String, Command] = that match {
      case REST => Right(REST)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Wield a weapon */
  case class Wield(slot: Slot) extends Command {
    override val keypresses: Seq[Char] = Seq('w', slot.label)

    override def merge(that: Command): Either[String, Command] = that match {
      case Wield(thatSlot) => for (inferredSlot <- slot.merge(thatSlot)) yield Wield(inferredSlot)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up */
  case object UP extends Command {
    override val keypresses: Seq[Char] = Seq('j')

    override def merge(that: Command): Either[String, Command] = that match {
      case UP => Right(UP)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down */
  case object DOWN extends Command {
    override val keypresses: Seq[Char] = Seq('k')

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWN => Right(DOWN)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move left */
  case object LEFT extends Command {
    override val keypresses: Seq[Char] = Seq('h')

    override def merge(that: Command): Either[String, Command] = that match {
      case LEFT => Right(LEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move right */
  case object RIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('l')

    override def merge(that: Command): Either[String, Command] = that match {
      case RIGHT => Right(RIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up left */
  case object UPLEFT extends Command {
    override val keypresses: Seq[Char] = Seq('y')

    override def merge(that: Command): Either[String, Command] = that match {
      case UPLEFT => Right(UPLEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move up right */
  case object UPRIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('u')

    override def merge(that: Command): Either[String, Command] = that match {
      case UPRIGHT => Right(UPRIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down left */
  object DOWNLEFT extends Command {
    override val keypresses: Seq[Char] = Seq('b')

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNLEFT => Right(DOWNLEFT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Move down right */
  case object DOWNRIGHT extends Command {
    override val keypresses: Seq[Char] = Seq('n')

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNRIGHT => Right(DOWNRIGHT)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  /** Go downstairs */
  case object DOWNSTAIRS extends Command {
    override val keypresses: Seq[Char] = Seq('>')

    override def merge(that: Command): Either[String, Command] = that match {
      case DOWNSTAIRS => Right(DOWNSTAIRS)
      case _ => Left(s"Incompatible commands: $this and $that")
    }
  }

  implicit def domain: Domain[Command] = (x: Command, y: Command) => x.merge(y)
}
