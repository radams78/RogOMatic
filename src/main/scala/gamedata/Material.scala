package gamedata

/** The set of materials that a wand or staff can be made of */
object Material extends ParsableEnum {
  type Material = Value
  override val setName: String = "material"
  val STEEL: gamedata.Material.Value = Value("steel")
  val BRONZE: gamedata.Material.Value = Value("bronze")
  val GOLD: gamedata.Material.Value = Value("gold")
  val SILVER: gamedata.Material.Value = Value("silver")
  val COPPER: gamedata.Material.Value = Value("copper")
  val NICKEL: gamedata.Material.Value = Value("nickel")
  val COBALT: gamedata.Material.Value = Value("cobalt")
  val TIN: gamedata.Material.Value = Value("tin")
  val IRON: gamedata.Material.Value = Value("iron")
  val MAGNESIUM: gamedata.Material.Value = Value("magnesium")
  val CHROME: gamedata.Material.Value = Value("chrome")
  val CARBON: gamedata.Material.Value = Value("carbon")
  val PLATINEM: gamedata.Material.Value = Value("platinum")
  val SILICON: gamedata.Material.Value = Value("silicon")
  val TITANIUM: gamedata.Material.Value = Value("titanium")
  val TEAK: gamedata.Material.Value = Value("teak")
  val OAK: gamedata.Material.Value = Value("oak")
  val CHERRY: gamedata.Material.Value = Value("cherry")
  val BIRCH: gamedata.Material.Value = Value("birch")
  val PINE: gamedata.Material.Value = Value("pine")
  val CEDAR: gamedata.Material.Value = Value("cedar")
  val REDWOOD: gamedata.Material.Value = Value("redwood")
  val BALSA: gamedata.Material.Value = Value("balsa")
  val IVORY: gamedata.Material.Value = Value("ivory")
  val WALNUT: gamedata.Material.Value = Value("walnut")
  val MAPLE: gamedata.Material.Value = Value("maple")
  val MAHOGANY: gamedata.Material.Value = Value("mahogany")
  val ELM: gamedata.Material.Value = Value("elm")
  val PALM: gamedata.Material.Value = Value("palm")
  val WOOD: gamedata.Material.Value = Value("wooden")

  implicit def domain: Domain[Material] = Domain.flatDomain
}
