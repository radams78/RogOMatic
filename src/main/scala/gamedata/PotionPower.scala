package gamedata

import domain.Domain

object PotionPower extends ParsableEnum {

  type PotionPower = Value

  implicit def valueToPotionPowertVal(x: Value): Val = x.asInstanceOf[Val]

  val BLINDNESS: PotionPower = Val("blindness")
  val CONFUSION: PotionPower = Val("confusion")
  val DETECT_MONSTER: PotionPower = Val("detect monster")
  val DETECT_THINGS: PotionPower = Val("detect things")
  val EXTRA_HEALING: PotionPower = Val("extra healing")
  val HALLUCINATION: PotionPower = Val("hallucination")
  val HASTE_SELF: PotionPower = Val("haste self")
  val HEALING: PotionPower = Val("healing")
  val INCREASE_STRENGTH: PotionPower = Val("increase strength")
  val LEVITATION: PotionPower = Val("levitation")
  val POISON: PotionPower = Val("poison")
  val RAISE_LEVEL: PotionPower = Val("raise level")
  val RESTORE_STRENGTH: PotionPower = Val("restore strength")
  val SEE_INVISIBLE: PotionPower = Val("see invisible")

  protected case class Val(effect: String) extends super.Val

  implicit def domain: Domain[Value] = Domain.flatDomain

  override val name: String = "potion power"
}