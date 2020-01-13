package gamedata.item.weapon

import domain.Domain
import gamedata.ParsableEnum
import gamedata.item.weapon.WieldableType.WieldableType

/** An enum for the set of weapon types in the game of Rogue */
sealed trait WeaponType

/** The melee weapons and bows. These weapons cannot be stacked. */
object WieldableType extends ParsableEnum {
  type WieldableType = Value

  val MACE: WieldableType = Val("mace")
  val LONG_SWORD: WieldableType = Val("long sword")
  val TWO_HANDED_SWORD: WieldableType = Val("two-handed sword")

  protected case class Val(override val name: String) extends super.Val(name) with WeaponType

  override implicit def valueToVal(x: WieldableType): Val = x.asInstanceOf[Val]

  override val name: String = "wieldable type"
}

object Missiletype extends Enumeration {
  type MissileType = Value

  val DART: MissileType = Val("dart", "darts")
  val ARROW: MissileType = Val("arrow", "arrows")
  val DAGGER: MissileType = Val("dagger", "daggers")
  val SHURIKEN: MissileType = Val("shuriken", "shurikens")

  protected case class Val(singular: String, plural: String) extends super.Val with WeaponType

  implicit def valueToVal(x: MissileType): Val = x.asInstanceOf[Val]
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
  val MACE: WieldableType = WieldableType.MACE

  /** Given the name of a weapon, return the appropriate [[WeaponType]], or an error message if weapon type could not
   * be recognised */
  def parse(description: String): Either[String, WeaponType] = description match {
    case "short bow" => Right(SHORT_BOW)
    case "dart" | "darts" => Right(DART)
    case "arrow" | "arrows" => Right(ARROW)
    case "dagger" | "daggers" => Right(DAGGER)
    case "shuriken" | "shurikens" => Right(SHURIKEN)
    case "mace" => Right(WieldableType.MACE)
    case "long sword" => Right(WieldableType.LONG_SWORD)
    case "two-handed sword" => Right(WieldableType.TWO_HANDED_SWORD)
    case _ => Left(s"Unrecognised weapon type: $description")
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

  case object SHORT_BOW extends WeaponType
}

