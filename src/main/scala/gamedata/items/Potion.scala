package gamedata.items

import domain.Domain
import domain.Domain._
import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamedata.{Fact, UsesKnowledge}

trait MagicItemType {
  type Attribute

  implicit def attributeDomain: Domain[Attribute]

  type Power

  implicit def powerDomain: Domain[Power]

  def singular: String

  def plural: String

  case class MagicItem(quantity: Option[Int], attribute: Option[Attribute], power: Option[Power]) extends Item {
    def merge(that: MagicItem): Either[String, MagicItem] = that match {
      case MagicItem(thatQuantity, thatAttribute, thatPower) => for {
        inferredQuantity <- quantity.merge(thatQuantity)
        inferredAttribute <- attribute.merge(thatAttribute)
        inferredPower <- power.merge(thatPower)
      } yield MagicItem(inferredQuantity, inferredAttribute, inferredPower)
    }

    override def merge(that: Item): Either[String, Item] = that match {
      case magicItem: MagicItem => merge(magicItem)
      case Item.UNKNOWN => Right(this)
      case _ => Left(s"Incompatible information: $this and $that")
    }

    override def implications: Set[Fact] = (attribute, power) match {
      case (Some(a), Some(p)) => Set(MagicItemKnowledge(a, p))
      case _ => Set()
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

  object MagicItem {
    implicit def domain: Domain[MagicItem] = (x: MagicItem, y: MagicItem) => x.merge(y)

    implicit def usesKnowledge: UsesKnowledge[MagicItem] = (self: MagicItem, fact: Fact) => (fact, self.attribute, self.power) match {
      case (MagicItemKnowledge(_a, _p), Some(a), Some(p)) if (a == _a && p != _p) || (a != _a && p == _p) =>
        Left(s"Incompatible information: $a -> $p and ${_a} -> ${_p}")
      case (MagicItemKnowledge(_a, _p: Power), Some(a), None) if a == _a => Right(MagicItem(self.quantity, Some(a), Some(_p)))
      case (MagicItemKnowledge(_a: Attribute, _p), None, Some(p)) if p == _p => Right(MagicItem(self.quantity, Some(_a), Some(p)))
      case _ => Right(self)
    }
  }

  case class MagicItemKnowledge(attribute: Attribute, power: Power) extends Fact

}

object PotionType extends MagicItemType {
  override type Attribute = Colour

  override implicit def attributeDomain: Domain[Attribute] = Colour.domain

  override type Power = PotionPower

  override implicit def powerDomain: Domain[Power] = PotionPower.domain

  override def singular: String = "potion"

  override def plural: String = "potions"
}

object Potion {
  type Potion = PotionType.MagicItem
  type PotionKnowledge = PotionType.MagicItemKnowledge

  implicit def domain: Domain[Potion] = PotionType.MagicItem.domain

  def apply(quantity: Int, colour: Colour): Potion = PotionType.MagicItem(Some(quantity), Some(colour), None)

  def apply(power: PotionPower): Potion = PotionType.MagicItem(None, None, Some(power))

  val UNKNOWN: Potion = PotionType.MagicItem(None, None, None)
}