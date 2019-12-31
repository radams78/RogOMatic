package gamedata.items

import domain.Domain
import gamedata.ParsableEnum

/** The set of armor types in the game of Rogue */
object ArmorType extends ParsableEnum {
  type ArmorType = Value
  override val name: String = "armor type"
  val LEATHER_ARMOR: ArmorType = Value("leather armor")
  val RING_MAIL: ArmorType = Value("ring mail")
  val SCALE_MAIL: ArmorType = Value("scale mail")
  val CHAIN_MAIL: ArmorType = Value("chain mail")
  val BANDED_MAIL: ArmorType = Value("banded mail")
  val SPLINT_MAIL: ArmorType = Value("splint mail")
  val PLATE_MAIL: ArmorType = Value("plate mail")

  implicit def domain: Domain[ArmorType] = Domain.flatDomain
}
