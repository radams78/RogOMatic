package gamedata

import domain.Domain

object MonsterType extends ParsableEnum {

  type MonsterType = Value
  val HOBGOBLIN: MonsterType = Val("hobgoblin")

  protected case class Val(name: String) extends super.Val

  implicit def toVal(x: Value): Val = x.asInstanceOf[Val]

  implicit def domain: Domain[MonsterType] = Domain.flatDomain

  override val setName: String = "monster type"
}
