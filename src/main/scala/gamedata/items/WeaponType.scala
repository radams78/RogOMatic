package gamedata.items

import domain.Domain

/** An enum for the set of weapon types in the game of Rogue */
sealed trait WeaponType

/** The melee weapons and bows. These weapons cannot be stacked. */
sealed trait WieldableType extends WeaponType {
  val name: String

  override def toString: String = name
}

object WieldableType {
  implicit def domain: Domain[WieldableType] = Domain.flatDomain
}

/** The types of missile */
sealed trait MissileType extends WeaponType {
  /** Name of the weapon in the singular */
  val singular: String

  /** Name of the weapon in the plural */
  val plural: String
}

object MissileType {
  implicit def domain: Domain[MissileType] = Domain.flatDomain
}

object WeaponType {
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
