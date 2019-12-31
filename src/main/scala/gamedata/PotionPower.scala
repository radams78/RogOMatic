package gamedata

import domain.Domain

object PotionPower extends ParsableEnum {

  type PotionPower = Value

  implicit def valueToPotionPowertVal(x: Value): Val = x.asInstanceOf[Val]

  val HEALING: PotionPower = Val("you begin to feel better")

  protected case class Val(effect: String) extends super.Val

  implicit def domain: Domain[Value] = Domain.flatDomain

  override val setName: String = "potion power"
}