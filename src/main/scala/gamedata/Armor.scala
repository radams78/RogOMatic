package gamedata

trait Armor extends Item

case class IdentifiedArmor(armorType: ArmorType, bonus: Bonus) extends Armor {
  override def toString: String = s"$bonus $armorType"
}

case class UnidentifiedArmor(armorType: ArmorType) extends Armor {
  override def toString: String = armorType.toString
}

object Armor {
  def apply(armorType: ArmorType, bonus: Bonus): Armor = IdentifiedArmor(armorType, bonus)

  def apply(armorType: ArmorType, bonus: Int): Armor = IdentifiedArmor(armorType, Bonus(bonus))

  def apply(armorType: ArmorType): Armor = UnidentifiedArmor(armorType)
}