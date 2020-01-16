package gamedata.item.magic

import domain.Domain
import domain.Domain._
import gamedata.item.Item
import gamedata.{Fact, UsesKnowledge}

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
  trait MagicItem extends Item {
    /** Quantity of items in the stack, if known */
    def quantity: Option[Int]
    
    def attribute: Option[Attribute]

    def power: Option[Power]

    def merge(that: MagicItem): Either[String, MagicItem]

    def build(attribute: Attribute, power: Power): MagicItem

    override def merge(that: Item): Either[String, Item] = that match {
      case magicItem: MagicItem => merge(magicItem)
      case Item.UNKNOWN => Right(this)
      case _ => Left(s"Incompatible information: $this and $that")
    }

    override def implications: Set[Fact] = (attribute, power) match {
      case (Some(a), Some(p)) => Set(MagicItemKnowledge(a, p))
      case _ => Set()
    }

    override def infer(fact: Fact): Either[String, Item] = fact match {
      case MagicItemKnowledge(_attribute, _power) if attribute.contains(_attribute) || power.contains(_power) =>
        merge(build(_attribute, _power))
      case _ => Right(this)
    }
  }

  case class MagicItemKnowledge(attribute: Attribute, power: Power) extends Fact

}

trait StackableMagicItemType extends MagicItemType {
  def singular: String

  def plural: String

  case class StackableMagicItem(quantity: Option[Int], attribute: Option[Attribute], power: Option[Power]) extends MagicItem {
    override def merge(that: MagicItem): Either[String, StackableMagicItem] = that match {
      case StackableMagicItem(thatQuantity, thatAttribute, thatPower) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredAttribute <- attribute.merge(thatAttribute)
        inferredPower <- power.merge(thatPower)
      } yield StackableMagicItem(inferredQuantity, inferredAttribute, inferredPower)
    }

    override def toString: String =
      (quantity match {
        case Some(q) => q.toString
        case None => "some"
      }) +
        (attribute match {
          case Some(a) => " " + a.toString
          case None => ""
        }) + " " +
        (if (quantity.contains(1)) singular else plural) +
        (power match {
          case Some(p) => " " + p.toString
          case None => ""
        })

    override def build(attribute: Attribute, power: Power): MagicItem =
      StackableMagicItem(None, Some(attribute), Some(power))
  }

  object StackableMagicItem {
    implicit def domain: Domain[StackableMagicItem] = (x: StackableMagicItem, y: StackableMagicItem) => x.merge(y)

    implicit def usesKnowledge: UsesKnowledge[StackableMagicItem] = (self: StackableMagicItem, fact: Fact) => (fact, self.attribute, self.power) match {
      case (MagicItemKnowledge(_a, _p), Some(a), Some(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (MagicItemKnowledge(_a, _p), Some(a), None) if a == _a => Right(StackableMagicItem(self.quantity, Some(a), Some(_p)))
      case (MagicItemKnowledge(_a, _p), None, Some(p)) if p == _p => Right(StackableMagicItem(self.quantity, Some(_a), Some(p)))
      case _ => Right(self)
    }
  }

}
