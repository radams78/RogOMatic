package gamedata.item.magic

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.fact.Fact
import gamedata.item.pItem
import gamedata.{UsesKnowledge, pCommand}

/** A type of item that has a magic power, and an attribute (e.g. colour, gem, material). When the item is first found,
 * we see only its attribute. When we learn what its power is, we record this [[Fact]] that that attribute is mapped to
 * that power. */
trait MagicItemType {
  /** The type of attributes for these magic items */
  type Attribute

  implicit def attributeDomain: Domain[Attribute]

  /** The type of powers that these magic items can have */
  type Power

  implicit def powerDomain: Domain[Power]

  /** There should be a class whose objects represent the stacks of magic items of this type. 
   * This class should extend this trait.
   *
   * Contract:
   * - implications is monotone
   * - x <= x.infer(fact)
   * - if x.implications contains fact then x.infer(fact) == x 
   * - build(a, p).attribute == Some(a) 
   * - build(a, p).power == Some(p) */
  trait MagicItem extends pItem {
    /** Quantity of items in the stack, if known */
    def quantity: pLift[Int]

    def attribute: pLift[Attribute]

    def power: pLift[Power]

    def _merge(that: MagicItem): Either[String, MagicItem]

    def build(attribute: Attribute, power: Power): MagicItem

    override def merge(that: pItem): Either[String, MagicItem] = that match {
      case magicItem: MagicItem => _merge(magicItem)
      case pItem.UNKNOWN => Right(this)
      case _ => Left(s"Incompatible information: $this and $that")
    }

    override def implications: Set[Fact] = (attribute, power) match {
      case (pLift.Known(a), pLift.Known(p)) => Set(MagicItemKnowledge(a, p))
      case _ => Set()
    }

    override def infer(fact: Fact): Either[String, MagicItem] = fact match {
      case MagicItemKnowledge(_attribute, _power) if attribute == pLift.Known(_attribute) || power == pLift.Known(_power) =>
        _merge(build(_attribute, _power))
      case _ => Right(this)
    }
  }

  case class MagicItemKnowledge(attribute: Attribute, power: Power) extends Fact {
    override def after(command: pCommand): Either[String, Set[Fact]] = Right(Set(this))
  }

}

trait UnstackableMagicItemType extends MagicItemType {

  trait UnstackableMagicItem extends MagicItem {
    override def quantity: pLift[Int] = pLift.Known(1)

    override def consumeOne: pLift[Option[pItem]] = pLift.Known(None)
  }

}

trait StackableMagicItemType extends MagicItemType {
  def singular: String

  def plural: String

  case class StackableMagicItem(quantity: pLift[Int], attribute: pLift[Attribute], power: pLift[Power]) extends MagicItem {
    override def _merge(that: MagicItem): Either[String, StackableMagicItem] = that match {
      case StackableMagicItem(thatQuantity, thatAttribute, thatPower) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredAttribute <- attribute.merge(thatAttribute)
        inferredPower <- power.merge(thatPower)
      } yield StackableMagicItem(inferredQuantity, inferredAttribute, inferredPower)
    }

    override def merge(that: pItem): Either[String, StackableMagicItem] = that match {
      case magicItem: MagicItem => _merge(magicItem)
      case pItem.UNKNOWN => Right(this)
      case _ => Left(s"Incompatible information: $this and $that")
    }

    override def toString: String =
      (quantity match {
        case pLift.Known(q) => q.toString
        case pLift.UNKNOWN => "some"
      }) +
        (attribute match {
          case pLift.Known(a) => " " + a.toString
          case pLift.UNKNOWN => ""
        }) + " " +
        (if (quantity == pLift.Known(1)) singular else plural) +
        (power match {
          case pLift.Known(p) => " " + p.toString
          case pLift.UNKNOWN => ""
        })

    override def build(attribute: Attribute, power: Power): MagicItem =
      StackableMagicItem(pLift.UNKNOWN, pLift.Known(attribute), pLift.Known(power))

    override def infer(fact: Fact): Either[String, StackableMagicItem] = fact match {
      case MagicItemKnowledge(_attribute, _power) if attribute == pLift.Known(_attribute) || power == pLift.Known(_power) =>
        _merge(build(_attribute, _power))
      case _ => Right(this)
    }

    override def consumeOne: pLift[Option[pItem]] = quantity match {
      case pLift.UNKNOWN => pLift.UNKNOWN
      case pLift.Known(1) => pLift.Known(None)
      case pLift.Known(q) => pLift.Known(Some(StackableMagicItem(pLift.Known(q - 1), attribute, power)))
    }
  }

  object StackableMagicItem {
    implicit def domain: Domain[StackableMagicItem] = (x: StackableMagicItem, y: StackableMagicItem) => x.merge(y)

    implicit def usesKnowledge: UsesKnowledge[StackableMagicItem] = (self: StackableMagicItem, fact: Fact) => (fact, self.attribute, self.power) match {
      case (MagicItemKnowledge(_a, _p), pLift.Known(a), pLift.Known(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (MagicItemKnowledge(_a, _p), pLift.Known(a), pLift.UNKNOWN) if a == _a => Right(StackableMagicItem(self.quantity, pLift.Known(a), pLift.Known(_p)))
      case (MagicItemKnowledge(_a, _p), pLift.UNKNOWN, pLift.Known(p)) if p == _p => Right(StackableMagicItem(self.quantity, pLift.Known(_a), pLift.Known(p)))
      case _ => Right(self)
    }
  }

}
