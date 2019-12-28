package gamedata

/** The two types of wand-like object in Rogue */
object WandType extends ParsableEnum {
  type WandType = Value
  override val setName: String = "wand type"
  val STAFF: gamedata.WandType.Value = Value("staff")
  val WAND: gamedata.WandType.Value = Value("wand")

  implicit def domain: Domain[WandType] = Domain.flatDomain
}
