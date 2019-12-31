package gamedata

import domain.Domain

/** The gems that a ring can have */
object Gem extends ParsableEnum {
  type Gem = Value
  override val setName: String = "gem"
  val DIAMOND: gamedata.Gem.Value = Value("diamond")
  val STIBOTANTALITE: gamedata.Gem.Value = Value("stibotantalite")
  val LAPI_LAZULI: gamedata.Gem.Value = Value("lapi-lazuli")
  val RUBY: gamedata.Gem.Value = Value("ruby")
  val EMERALD: gamedata.Gem.Value = Value("emerald")
  val SAPPHIRE: gamedata.Gem.Value = Value("sapphire")
  val AMETHYST: gamedata.Gem.Value = Value("amethyst")
  val QUARTZ: gamedata.Gem.Value = Value("quartz")
  val TIGER_EYE: gamedata.Gem.Value = Value("tiger-eye")
  val OPAL: gamedata.Gem.Value = Value("opal")
  val AGATE: gamedata.Gem.Value = Value("agate")
  val TURQUOISE: gamedata.Gem.Value = Value("turquoise")
  val PEARL: gamedata.Gem.Value = Value("pearl")
  val GARNET: gamedata.Gem.Value = Value("garnet")

  implicit def domain: Domain[Gem] = Domain.flatDomain
}
