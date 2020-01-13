package gamedata.item.magic.ring

import domain.Domain
import gamedata.ParsableEnum

/** The set of powers that a ring can have */
object RingPower extends ParsableEnum {

  type RingPower = Value

  implicit def valueToScrollPowertVal(x: Value): Val = x.asInstanceOf[Val]

  override val name: String = "ring power"
  val ADD_STRENGTH: RingPower = Val("add strength")
  val ADORNMENT: RingPower = Val("adornment")
  val DEXTERITY: RingPower = Val("dexterity")
  val MAINTAIN_ARMOR: RingPower = Val("maintain armor")
  val REGENERATION: RingPower = Val("regeneration")
  val SEARCHING: RingPower = Val("searching")
  val SEE_INVISIBLE: RingPower = Val("see invisible")
  val SLOW_DIGESTION: RingPower = Val("slow digestion")
  val STEALTH: RingPower = Val("stealth")
  val SUSTAIN_STRENGTH: RingPower = Val("sustain strength")
  val TELEPORTATION: RingPower = Val("teleportation")

  implicit def domain: Domain[RingPower] = Domain.flatDomain

  protected case class Val(effect: String) extends super.Val

}
