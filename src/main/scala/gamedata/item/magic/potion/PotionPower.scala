package gamedata.item.magic.potion

import gamedata.ParsableEnum

object PotionPower extends ParsableEnum {
  type PotionPower = Value

  override val name: String = "potion power"
  val BLINDNESS: PotionPower = Value("blindness")
  val CONFUSION: PotionPower = Value("confusion")
  val DETECT_MONSTER: PotionPower = Value("detect monster")
  val DETECT_THINGS: PotionPower = Value("detect things")
  val EXTRA_HEALING: PotionPower = Value("extra healing")
  val HALLUCINATION: PotionPower = Value("hallucination")
  val HASTE_SELF: PotionPower = Value("haste self")
  val HEALING: PotionPower = Value("healing")
  val INCREASE_STRENGTH: PotionPower = Value("increase strength")
  val LEVITATION: PotionPower = Value("levitation")
  val POISON: PotionPower = Value("poison")
  val RAISE_LEVEL: PotionPower = Value("raise level")
  val RESTORE_STRENGTH: PotionPower = Value("restore strength")
  val SEE_INVISIBLE: PotionPower = Value("see invisible")
}
