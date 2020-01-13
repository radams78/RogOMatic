package gamedata.item.magic.wand

import gamedata.ParsableEnum

/** The two types of wand-like object in Rogue */
object WandType extends ParsableEnum {
  type WandType = Value
  override val name: String = "wand type"
  val STAFF: WandType = Val("staff")
  val WAND: WandType = Val("wand")

  protected case class Val(override val name: String) extends super.Val(name)

}
