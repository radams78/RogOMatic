package gamedata.item.weapon

import gamedata.ParsableEnum

object Missiletype extends ParsableEnum {
  type MissileType = Value

  val DART: MissileType = Val("dart", "darts")
  val ARROW: MissileType = Val("arrow", "arrows")
  val DAGGER: MissileType = Val("dagger", "daggers")
  val SHURIKEN: MissileType = Val("shuriken", "shurikens")

  protected case class Val(singular: String, plural: String) extends super.Val(singular) with WeaponType

  override implicit def valueToVal(x: MissileType): Val = x.asInstanceOf[Val]

  override val name: String = "missile type"
}
