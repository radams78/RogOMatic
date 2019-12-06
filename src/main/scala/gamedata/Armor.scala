package gamedata

case class Armor(armorType: ArmorType, bonus: Bonus) extends Item {
  override def toString: String = s"$bonus $armorType"
}

object Armor {
  def apply(armorType: ArmorType, bonus: Int): Armor = Armor(armorType, Bonus(bonus))
}