package gamedata.item.armor

import domain.Domain._
import gamedata.item.armor.ArmorType.ArmorType
import gamedata.item.{Bonus, Item, armor}

/** A suit of armor */
trait Armor extends Item

/** An identified suit of armor */
case class IdentifiedArmor(armorType: ArmorType, bonus: Bonus) extends Armor {
  override def toString: String = s"$bonus $armorType"

  override def merge(that: Item): Either[String, Item] = that match {
    case IdentifiedArmor(thatArmorType, thatBonus) => for {
      inferredArmorType <- armorType.merge(thatArmorType)
      inferredBonus <- bonus.merge(thatBonus)
    } yield IdentifiedArmor(inferredArmorType, inferredBonus)
    case UnidentifiedArmor(thatArmorType) => for {
      inferredAmorType <- armorType.merge(thatArmorType)
    } yield IdentifiedArmor(inferredAmorType, bonus)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}

/** An unidentified suit of armor */
case class UnidentifiedArmor(armorType: ArmorType) extends Armor {
  override def toString: String = armorType.toString

  override def merge(that: Item): Either[String, Item] = that match {
    case IdentifiedArmor(thatArmorType, thatBonus) => for {
      inferredArmorType <- armorType.merge(thatArmorType)
    } yield armor.IdentifiedArmor(inferredArmorType, thatBonus)
    case UnidentifiedArmor(thatArmorType) => for {
      inferredArmorType <- armorType.merge(thatArmorType)
    } yield UnidentifiedArmor(inferredArmorType)
    case _ => Left(s"Incompatible item: $this and $that")
  }
}

/** Factory methods for [[Armor]] */
object Armor {
  def apply(armorType: ArmorType, bonus: Bonus): Armor = IdentifiedArmor(armorType, bonus)

  def apply(armorType: ArmorType, bonus: Int): Armor = IdentifiedArmor(armorType, Bonus(bonus))

  def apply(armorType: ArmorType): Armor = UnidentifiedArmor(armorType)
}