package gamedata.item.magic.ring

import gamedata.ParsableEnum

/** The gems that a ring can have */
object Gem extends ParsableEnum {
  type Gem = Value
  override val name: String = "gem"
  val DIAMOND: Gem = Val("diamond")
  val STIBOTANTALITE: Gem = Val("stibotantalite")
  val LAPI_LAZULI: Gem = Val("lapi-lazuli")
  val RUBY: Gem = Val("ruby")
  val EMERALD: Gem = Val("emerald")
  val SAPPHIRE: Gem = Val("sapphire")
  val AMETHYST: Gem = Val("amethyst")
  val QUARTZ: Gem = Val("quartz")
  val TIGER_EYE: Gem = Val("tiger-eye")
  val OPAL: Gem = Val("opal")
  val AGATE: Gem = Val("agate")
  val TURQUOISE: Gem = Val("turquoise")
  val PEARL: Gem = Val("pearl")
  val GARNET: Gem = Val("garnet")

  protected case class Val(override val name: String) extends super.Val(name)

}
