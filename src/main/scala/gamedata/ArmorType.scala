package gamedata

/** The set of armor types in the game of Rogue */
sealed trait ArmorType {
  val name: String

  override def toString: String = name
}

object ArmorType {
  val ALL: Set[ArmorType] = Set(LEATHER_ARMOR, RING_MAIL, SCALE_MAIL, CHAIN_MAIL, BANDED_MAIL, SPLINT_MAIL, PLATE_MAIL)

  def parse(armorType: String): Option[ArmorType] = ALL.find(_.name == armorType)

  case object LEATHER_ARMOR extends ArmorType {
    override val name: String = "leather armor"
  }

  case object RING_MAIL extends ArmorType {
    override val name: String = "ring mail"
  }

  case object SCALE_MAIL extends ArmorType {
    override val name: String = "scale mail"
  }

  case object CHAIN_MAIL extends ArmorType {
    override val name: String = "chain mail"
  }

  case object BANDED_MAIL extends ArmorType {
    override val name: String = "banded mail"
  }

  case object SPLINT_MAIL extends ArmorType {
    override val name: String = "splint mail"
  }

  case object PLATE_MAIL extends ArmorType {
    override val name: String = "plate mail"
  }

}