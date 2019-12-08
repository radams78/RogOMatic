package gamedata

/** The gems that a ring can have */
sealed trait Gem

object Gem {
  def parse(gem: String): Either[String, Gem] = gem match {
    case "diamond" => Right(DIAMOND)
    case "stibotantalite" => Right(STIBOTANTALITE)
    case "lapi-lazuli" => Right(LAPI_LAZULI)
    case "ruby" => Right(RUBY)
    case "emerald" => Right(EMERALD)
    case "sapphire" => Right(SAPPHIRE)
    case "amethyst" => Right(AMETHYST)
    case "quartz" => Right(QUARTZ)
    case "tiger-eye" => Right(TIGER_EYE)
    case "opal" => Right(OPAL)
    case "agate" => Right(AGATE)
    case "turquoise" => Right(TURQUOISE)
    case "pearl" => Right(PEARL)
    case "garnet" => Right(GARNET)
    case _ => Left(s"Unrecognised gem: $gem")
  }

  case object DIAMOND extends Gem

  case object STIBOTANTALITE extends Gem

  case object LAPI_LAZULI extends Gem

  case object RUBY extends Gem

  case object EMERALD extends Gem

  case object SAPPHIRE extends Gem

  case object AMETHYST extends Gem

  case object QUARTZ extends Gem

  case object TIGER_EYE extends Gem

  case object OPAL extends Gem

  case object AGATE extends Gem

  case object TURQUOISE extends Gem

  case object PEARL extends Gem

  case object GARNET extends Gem

}
