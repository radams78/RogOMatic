package gamedata.item.armor

import gamedata.ParsableEnum

/** The set of armor types in the game of Rogue */
object ArmorType extends ParsableEnum {
  type ArmorType = Value
  override val name: String = "armor type"
  val LEATHER_ARMOR: ArmorType = Val("leather armor")
  val RING_MAIL: ArmorType = Val("ring mail")
  val SCALE_MAIL: ArmorType = Val("scale mail")
  val CHAIN_MAIL: ArmorType = Val("chain mail")
  val BANDED_MAIL: ArmorType = Val("banded mail")
  val SPLINT_MAIL: ArmorType = Val("splint mail")
  val PLATE_MAIL: ArmorType = Val("plate mail")

  protected case class Val(override val name: String) extends super.Val(name)

}
