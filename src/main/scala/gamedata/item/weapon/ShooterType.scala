package gamedata.item.weapon

import gamedata.ParsableEnum

/** The melee weapons and bows. These weapons cannot be stacked. */
object ShooterType extends ParsableEnum {
  type ShooterType = Value

  val SHORT_BOW: ShooterType = Val("short bow")

  protected case class Val(override val name: String) extends super.Val(name) with WeaponType

  override implicit def valueToVal(x: ShooterType): Val = x.asInstanceOf[Val]

  override val name: String = "missile weapon type"
}
