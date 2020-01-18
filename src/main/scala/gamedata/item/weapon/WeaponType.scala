package gamedata.item.weapon

import gamedata.item.weapon.MeleeType.MeleeType
import gamedata.item.weapon.Missiletype.MissileType

/** An enum for the set of weapon types in the game of Rogue */
trait WeaponType







object WeaponType {
  val SHORT_BOW: WeaponType = ShooterType.SHORT_BOW

  val ARROW: MissileType = Missiletype.ARROW

  val MACE: MeleeType = MeleeType.MACE

  /** Given the name of a weapon, return the appropriate [[WeaponType]], or an error message if weapon type could not
   * be recognised */
  def parse(description: String): Either[String, WeaponType] = description match {
    case "short bow" => Right(ShooterType.SHORT_BOW)
    case "dart" | "darts" => Right(Missiletype.DART)
    case "arrow" | "arrows" => Right(Missiletype.ARROW)
    case "dagger" | "daggers" => Right(Missiletype.DAGGER)
    case "shuriken" | "shurikens" => Right(Missiletype.SHURIKEN)
    case "mace" => Right(MeleeType.MACE)
    case "long sword" => Right(MeleeType.LONG_SWORD)
    case "two-handed sword" => Right(MeleeType.TWO_HANDED_SWORD)
    case _ => Left(s"Unrecognised weapon type: $description")
  }
}

