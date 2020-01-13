package gamedata.item

import domain.Domain
import domain.Domain._
import gamedata.{Fact, UsesKnowledge}

trait MagicItemType {
  type Attribute

  implicit def attributeDomain: Domain[Attribute]

  type Power

  implicit def powerDomain: Domain[Power]

  trait MagicItem extends Item {
    def quantity: Option[Int]

    def attribute: Option[Attribute]

    def power: Option[Power]

    def merge(that: MagicItem): Either[String, MagicItem]

    override def merge(that: Item): Either[String, Item] = that match {
      case magicItem: MagicItem => merge(magicItem)
      case Item.UNKNOWN => Right(this)
      case _ => Left(s"Incompatible information: $this and $that")
    }

    override def implications: Set[Fact] = (attribute, power) match {
      case (Some(a), Some(p)) => Set(MagicItemKnowledge(a, p))
      case _ => Set()
    }
  }

  case class MagicItemKnowledge(attribute: Attribute, power: Power) extends Fact

}

trait StackableMagicItemType extends MagicItemType {
  def singular: String

  def plural: String

  /** Contract: 
   * - implications is monotone */
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
  }

  object StackableMagicItem {
    implicit def domain: Domain[StackableMagicItem] = (x: StackableMagicItem, y: StackableMagicItem) => x.merge(y)

    implicit def usesKnowledge: UsesKnowledge[StackableMagicItem] = (self: StackableMagicItem, fact: Fact) => (fact, self.attribute, self.power) match {
      case (MagicItemKnowledge(_a, _p), Some(a), Some(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (MagicItemKnowledge(_a, _p: Power), Some(a), None) if a == _a => Right(StackableMagicItem(self.quantity, Some(a), Some(_p)))
      case (MagicItemKnowledge(_a: Attribute, _p), None, Some(p)) if p == _p => Right(StackableMagicItem(self.quantity, Some(_a), Some(p)))
      case _ => Right(self)
    }
  }

}

trait UnstackableMagicItemType extends MagicItemType {
  def name: String

  /** Contract: 
   * - implications is monotone */
  case class UnstackableMagicItem(attribute: Option[Attribute], power: Option[Power]) extends MagicItem {
    override def merge(that: MagicItem): Either[String, UnstackableMagicItem] = that match {
      case UnstackableMagicItem(thatAttribute, thatPower) => for {
        inferredAttribute <- attribute.merge(thatAttribute)
        inferredPower <- power.merge(thatPower)
      } yield UnstackableMagicItem(inferredAttribute, inferredPower)
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
        name +
        (power match {
          case Some(p) => " " + p.toString
          case None => ""
        })

    override def quantity: Option[Int] = Some(1)
  }

  object UnstackableMagicItem {
    implicit def domain: Domain[UnstackableMagicItem] = (x: UnstackableMagicItem, y: UnstackableMagicItem) => x.merge(y)

    implicit def usesKnowledge: UsesKnowledge[UnstackableMagicItem] = (self: UnstackableMagicItem, fact: Fact) => (fact, self.attribute, self.power) match {
      case (MagicItemKnowledge(_a, _p), Some(a), Some(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (MagicItemKnowledge(_a, _p: Power), Some(a), None) if a == _a => Right(UnstackableMagicItem(Some(a), Some(_p)))
      case (MagicItemKnowledge(_a: Attribute, _p), None, Some(p)) if p == _p => Right(UnstackableMagicItem(Some(_a), Some(p)))
      case _ => Right(self)
    }
  }

}