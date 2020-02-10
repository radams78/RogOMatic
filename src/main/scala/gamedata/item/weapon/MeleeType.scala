package gamedata.item.weapon

import gamedata.ParsableEnum

/** The melee weapons and bows. These weapons cannot be stacked. */
object MeleeType extends ParsableEnum {
  type MeleeType = Value

  val MACE: MeleeType = Val("mace")
  val LONG_SWORD: MeleeType = Val("long sword")
  val TWO_HANDED_SWORD: MeleeType = Val("two-handed sword")

  protected case class Val(override val name: String) extends super.Val(name) with WeaponType {
    override def toString(): String = name
  }

  override implicit def valueToVal(x: MeleeType): Val = x.asInstanceOf[Val]

  override val name: String = "wieldable type"
}
