package gamedata

/** The set of weapon types in the game of Rogue */
sealed trait WeaponType

object WeaponType {
  def parse(description: String): WeaponType = description match {
    case "short bow" => SHORT_BOW
    case "dart" => DART
    case "arrow" => ARROW
    case "dagger" => DAGGER
    case "shuriken" => SHURIKEN
    case "mace" => MACE
    case "long sword" => LONG_SWORD
    case "two-handed sword" => TWO_HANDED_SWORD
  }

  case object SHORT_BOW extends WeaponType

  case object DART extends WeaponType

  case object ARROW extends WeaponType

  case object DAGGER extends WeaponType

  case object SHURIKEN extends WeaponType

  case object MACE extends WeaponType

  case object LONG_SWORD extends WeaponType

  case object TWO_HANDED_SWORD extends WeaponType

}
