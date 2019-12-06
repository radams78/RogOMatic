package gamedata

/** The set of weapon types in the game of Rogue */
sealed trait WeaponType

sealed trait WieldableType extends WeaponType {
  val name: String

  override def toString: String = name
}

sealed trait MissileType extends WeaponType {
  val plural: String

  val singular: String
}

object WeaponType {
  def parse(description: String): WeaponType = description match {
    case "short bow" => SHORT_BOW
    case "dart" | "darts" => DART
    case "arrow" | "arrows" => ARROW
    case "dagger" | "daggers" => DAGGER
    case "shuriken" | "shurikens" => SHURIKEN
    case "mace" => MACE
    case "long sword" => LONG_SWORD
    case "two-handed sword" => TWO_HANDED_SWORD
  }

  case object SHORT_BOW extends WieldableType {
    override val name: String = "short bow"
  }

  case object DART extends MissileType {
    override val singular: String = "dart"

    override val plural: String = "darts"
  }

  case object ARROW extends MissileType {
    override val singular: String = "arrow"

    override val plural: String = "arrows"
  }

  case object DAGGER extends MissileType {
    override val singular: String = "dagger"

    override val plural: String = "daggers"
  }

  case object SHURIKEN extends MissileType {
    override val singular: String = "shuriken"

    override val plural: String = "shurikens"
  }

  case object MACE extends WieldableType {
    override val name: String = "mace"
  }

  case object LONG_SWORD extends WieldableType {
    override val name: String = "long sword"
  }

  case object TWO_HANDED_SWORD extends WieldableType {
    override val name: String = "two-handed sword"
  }

}
