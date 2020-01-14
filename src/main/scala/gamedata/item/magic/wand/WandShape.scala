package gamedata.item.magic.wand

import gamedata.ParsableEnum

/** The two types of wand-like object in Rogue */
object WandShape extends ParsableEnum {
  type WandShape = Value
  override val name: String = "wand type"
  val STAFF: WandShape = Val("staff")
  val WAND: WandShape = Val("wand")

  protected case class Val(override val name: String) extends super.Val(name)

}
