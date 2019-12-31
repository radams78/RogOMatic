package gamedata.items

import domain.Domain
import gamedata.ParsableEnum

/** The gems that a ring can have */
object Gem extends ParsableEnum {
  type Gem = Value
  override val name: String = "gem"
  val DIAMOND: Gem = Value("diamond")
  val STIBOTANTALITE: Gem = Value("stibotantalite")
  val LAPI_LAZULI: Gem = Value("lapi-lazuli")
  val RUBY: Gem = Value("ruby")
  val EMERALD: Gem = Value("emerald")
  val SAPPHIRE: Gem = Value("sapphire")
  val AMETHYST: Gem = Value("amethyst")
  val QUARTZ: Gem = Value("quartz")
  val TIGER_EYE: Gem = Value("tiger-eye")
  val OPAL: Gem = Value("opal")
  val AGATE: Gem = Value("agate")
  val TURQUOISE: Gem = Value("turquoise")
  val PEARL: Gem = Value("pearl")
  val GARNET: Gem = Value("garnet")

  implicit def domain: Domain[Gem] = Domain.flatDomain
}
