package gamedata.item.magic.wand

import domain.Domain
import gamedata.ParsableEnum

/** The set of materials that a wand or staff can be made of */
object Material extends ParsableEnum {
  type Material = Value
  override val name: String = "material"
  val STEEL: Material = Value("steel")
  val BRONZE: Material = Value("bronze")
  val GOLD: Material = Value("gold")
  val SILVER: Material = Value("silver")
  val COPPER: Material = Value("copper")
  val NICKEL: Material = Value("nickel")
  val COBALT: Material = Value("cobalt")
  val TIN: Material = Value("tin")
  val IRON: Material = Value("iron")
  val MAGNESIUM: Material = Value("magnesium")
  val CHROME: Material = Value("chrome")
  val CARBON: Material = Value("carbon")
  val PLATINEM: Material = Value("platinum")
  val SILICON: Material = Value("silicon")
  val TITANIUM: Material = Value("titanium")
  val TEAK: Material = Value("teak")
  val OAK: Material = Value("oak")
  val CHERRY: Material = Value("cherry")
  val BIRCH: Material = Value("birch")
  val PINE: Material = Value("pine")
  val CEDAR: Material = Value("cedar")
  val REDWOOD: Material = Value("redwood")
  val BALSA: Material = Value("balsa")
  val IVORY: Material = Value("ivory")
  val WALNUT: Material = Value("walnut")
  val MAPLE: Material = Value("maple")
  val MAHOGANY: Material = Value("mahogany")
  val ELM: Material = Value("elm")
  val PALM: Material = Value("palm")
  val WOOD: Material = Value("wooden")

  implicit def domain: Domain[Material] = Domain.flatDomain
}
