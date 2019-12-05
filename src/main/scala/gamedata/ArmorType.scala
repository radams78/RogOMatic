package gamedata

sealed trait ArmorType

object ArmorType {
  def parse(armorType: String): ArmorType = armorType match {
    case "leather armor" => LEATHER_ARMOR
    case "ring mail" => RING_MAIL
    case "scale mail" => SCALE_MAIL
    case "chain mail" => CHAIN_MAIL
    case "banded mail" => BANDED_MAIL
    case "splint mail" => SPLINT_MAIL
    case "plate mail" => PLATE_MAIL
  }

  case object LEATHER_ARMOR extends ArmorType

  case object RING_MAIL extends ArmorType

  case object SCALE_MAIL extends ArmorType

  case object CHAIN_MAIL extends ArmorType

  case object BANDED_MAIL extends ArmorType

  case object SPLINT_MAIL extends ArmorType

  case object PLATE_MAIL extends ArmorType

}