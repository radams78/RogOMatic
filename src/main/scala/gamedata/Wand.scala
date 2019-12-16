package gamedata

import gamedata.Material.Material
import gamedata.WandType.WandType

/** A wand or staff */
case class Wand(wandType: WandType, material: Material) extends Item {

}
