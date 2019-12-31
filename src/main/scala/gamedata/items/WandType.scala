package gamedata.items

import domain.Domain
import gamedata.ParsableEnum

/** The two types of wand-like object in Rogue */
object WandType extends ParsableEnum {
  type WandType = Value
  override val name: String = "wand type"
  val STAFF: WandType = Value("staff")
  val WAND: WandType = Value("wand")

  implicit def domain: Domain[WandType] = Domain.flatDomain
}
