package gamedata.item.magic.wand

import gamedata.ParsableEnum

/** The powers that a wand or staff can have */
object WandPower extends ParsableEnum {
  override val name: String = "wand power"
  type WandPower = Value

  val CANCELLATION: WandPower = Val("cancellation")
  val COLD: WandPower = Val("cold")
  val DO_NOTHING: WandPower = Val("do nothing")
  val DRAIN_LIFE: WandPower = Val("drain life")
  val FIRE: WandPower = Val("fire")
  val HASTE_MONSTER: WandPower = Val("haste monster")
  val INVISIBILITY: WandPower = Val("invisibility")
  val MAGIC_MISSILE: WandPower = Val("magic missile")
  val POLYMORPH: WandPower = Val("polymorph")
  val SLOW_MONSTER: WandPower = Val("slow monster")
  val TELEPORT_AWAY: WandPower = Val("teleport away")

  protected case class Val(override val name: String) extends super.Val(name)

}