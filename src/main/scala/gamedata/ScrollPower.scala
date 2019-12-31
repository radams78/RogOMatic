package gamedata

import domain.Domain

/** The set of powers that a scroll can have */
object ScrollPower extends ParsableEnum {

  type ScrollPower = Value

  implicit def valueToScrollPowertVal(x: Value): Val = x.asInstanceOf[Val]

  val AGGRAVATE_MONSTER: ScrollPower = Val("aggravate monster")
  val CREATE_MONSTER: ScrollPower = Val("create monster")
  val CONFUSE_MONSTER: ScrollPower = Val("confuse monster")
  val ENCHANT_ARMOR: ScrollPower = Val("enchant armor")
  val ENCHANT_WEAPON: ScrollPower = Val("enchant weapon")
  val HOLD_MONSTER: ScrollPower = Val("hold monster")
  val IDENTIFY: ScrollPower = Val("identify")
  val MAGIC_MAPPING: ScrollPower = Val("magic mapping")
  val PROTECT_ARMOR: ScrollPower = Val("protect armor")
  val REMOVE_CURSE: ScrollPower = Val("remove curse")
  val SCARE_MONSTER: ScrollPower = Val("scare monster")
  val SLEEP: ScrollPower = Val("sleep")
  val TELEPORTATION: ScrollPower = Val("teleportation")

  protected case class Val(effect: String) extends super.Val

  implicit def scrollPowerDomain: Domain[ScrollPower] = Domain.flatDomain

  override val name: String = "scroll power"
}
