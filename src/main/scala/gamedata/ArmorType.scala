package gamedata

/** The set of armor types in the game of Rogue */
object ArmorType extends ParsableEnum {
  type ArmorType = Value
  override val setName: String = "armor type"
  val LEATHER_ARMOR: gamedata.ArmorType.Value = Value("leather armor")
  val RING_MAIL: gamedata.ArmorType.Value = Value("ring mail")
  val SCALE_MAIL: gamedata.ArmorType.Value = Value("scale mail")
  val CHAIN_MAIL: gamedata.ArmorType.Value = Value("chain mail")
  val BANDED_MAIL: gamedata.ArmorType.Value = Value("banded mail")
  val SPLINT_MAIL: gamedata.ArmorType.Value = Value("splint mail")
  val PLATE_MAIL: gamedata.ArmorType.Value = Value("plate mail")
}
