package gamedata.item.weapon

import gamedata.item.weapon.MeleeType.MeleeType
import gamedata.item.weapon.Missiletype.MissileType

/** An enum for the set of weapon types in the game of Rogue */
trait WeaponType

object WeaponType {
  val SHORT_BOW: WeaponType = ShooterType.SHORT_BOW

  val ARROW: MissileType = Missiletype.ARROW

  val MACE: MeleeType = MeleeType.MACE

  val DART: WeaponType = Missiletype.DART

  val DAGGER: WeaponType = Missiletype.DAGGER

  val SHURIKEN: WeaponType = Missiletype.SHURIKEN

  val LONG_SWORD: WeaponType = MeleeType.LONG_SWORD

  val TWO_HANDED_SWORD: WeaponType = MeleeType.TWO_HANDED_SWORD

  /** Given the name of a weapon, return the appropriate [[WeaponType]], or an error message if weapon type could not
   * be recognised */
  def parse(description: String): Either[String, WeaponType] = description match {
    case "short bow" => Right(SHORT_BOW)
    case "dart" | "darts" => Right(DART)
    case "arrow" | "arrows" => Right(ARROW)
    case "dagger" | "daggers" => Right(DAGGER)
    case "shuriken" | "shurikens" => Right(SHURIKEN)
    case "mace" => Right(MACE)
    case "long sword" => Right(LONG_SWORD)
    case "two-handed sword" => Right(TWO_HANDED_SWORD)
    case _ => Left(s"Unrecognised weapon type: $description")
  }
}

