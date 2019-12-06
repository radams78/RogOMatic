package gamedata

case class Armor(armorType: ArmorType, bonus: Int) extends Item {
  override def toString: String = s"${if (bonus > 0) "+"}$bonus $armorType"
}
