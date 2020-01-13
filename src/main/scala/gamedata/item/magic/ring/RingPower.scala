package gamedata.item.magic.ring

import domain.Domain

trait RingPower

object RingPower {
  implicit def domain: Domain[RingPower] = Domain.flatDomain
}