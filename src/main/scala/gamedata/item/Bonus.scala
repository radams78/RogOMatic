package gamedata.item

import domain.Domain

case class Bonus(value: Int) {
  override def toString: String = (if (value >= 0) "+" else "") + value
}

object Bonus {
  implicit def domain: Domain[Bonus] = Domain.flatDomain
}
