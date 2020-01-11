package gamedata

import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamedata.items.ScrollPower.ScrollPower

trait Fact

object Fact {

  case class PotionKnowledge(colour: Colour, power: PotionPower) extends Fact

  case class ScrollKnowledge(title: String, power: ScrollPower) extends Fact

}

trait ProvidesKnowledge[T] {
  def implications(self: T): Set[Fact]
}

object ProvidesKnowledge {

  implicit final class ProvidesKnowledgeOps[T](self: T)(implicit s: ProvidesKnowledge[T]) {
    def implications: Set[Fact] = s.implications(self)
  }

  implicit def pOptionProvidesKnowledge[T](implicit s: ProvidesKnowledge[T]): ProvidesKnowledge[pOption[T]] = {
    case pOption.UNKNOWN => Set()
    case pOption.NONE => Set()
    case pOption.Some(t) => t.implications
  }

  implicit def factProvidesKnowledge: ProvidesKnowledge[Fact] = (self: Fact) => Set(self)
}



