package gamedata.item.magic.wand

import gamedata.ParsableEnum

/** The set of materials that a wand or staff can be made of */
object Material extends ParsableEnum {
  type Material = Value
  override val name: String = "material"
  val STEEL: Material = Val("steel")
  val BRONZE: Material = Val("bronze")
  val GOLD: Material = Val("gold")
  val SILVER: Material = Val("silver")
  val COPPER: Material = Val("copper")
  val NICKEL: Material = Val("nickel")
  val COBALT: Material = Val("cobalt")
  val TIN: Material = Val("tin")
  val IRON: Material = Val("iron")
  val MAGNESIUM: Material = Val("magnesium")
  val CHROME: Material = Val("chrome")
  val CARBON: Material = Val("carbon")
  val PLATINEM: Material = Val("platinum")
  val SILICON: Material = Val("silicon")
  val TITANIUM: Material = Val("titanium")
  val TEAK: Material = Val("teak")
  val OAK: Material = Val("oak")
  val CHERRY: Material = Val("cherry")
  val BIRCH: Material = Val("birch")
  val PINE: Material = Val("pine")
  val CEDAR: Material = Val("cedar")
  val REDWOOD: Material = Val("redwood")
  val BALSA: Material = Val("balsa")
  val IVORY: Material = Val("ivory")
  val WALNUT: Material = Val("walnut")
  val MAPLE: Material = Val("maple")
  val MAHOGANY: Material = Val("mahogany")
  val ELM: Material = Val("elm")
  val PALM: Material = Val("palm")
  val WOOD: Material = Val("wooden")

  protected case class Val(override val name: String) extends super.Val(name)

}
