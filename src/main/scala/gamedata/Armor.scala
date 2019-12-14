package gamedata

/** A suit of armor */
trait Armor extends Item

/** An identified suit of armor */
case class IdentifiedArmor(armorType: ArmorType, bonus: Bonus) extends Armor {
  override def toString: String = s"$bonus $armorType"
}

/** An unidentified suit of armor */
case class UnidentifiedArmor(armorType: ArmorType) extends Armor {
  override def toString: String = armorType.toString
}

/** Factory methods for [[Armor]] */
object Armor {
  def apply(armorType: ArmorType, bonus: Bonus): Armor = IdentifiedArmor(armorType, bonus)

  def apply(armorType: ArmorType, bonus: Int): Armor = IdentifiedArmor(armorType, Bonus(bonus))

  def apply(armorType: ArmorType): Armor = UnidentifiedArmor(armorType)
}