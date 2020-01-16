package gamedata.item.weapon

import domain.Domain
import gamedata.ParsableEnum
import gamedata.item.weapon.Missiletype.MissileType
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

  implicit def domain: Domain[MissileType] = Domain.flatDomain
}

object WeaponType {
  val ARROW: MissileType = Missiletype.ARROW

  val MACE: WieldableType = WieldableType.MACE

  /** Given the name of a weapon, return the appropriate [[WeaponType]], or an error message if weapon type could not
   * be recognised */
  def parse(description: String): Either[String, WeaponType] = description match {
    case "short bow" => Right(SHORT_BOW)
    case "dart" | "darts" => Right(Missiletype.DART)
    case "arrow" | "arrows" => Right(Missiletype.ARROW)
    case "dagger" | "daggers" => Right(Missiletype.DAGGER)
    case "shuriken" | "shurikens" => Right(Missiletype.SHURIKEN)
    case "mace" => Right(WieldableType.MACE)
    case "long sword" => Right(WieldableType.LONG_SWORD)
    case "two-handed sword" => Right(WieldableType.TWO_HANDED_SWORD)
    case _ => Left(s"Unrecognised weapon type: $description")
  }

  case object SHORT_BOW extends WeaponType
}

