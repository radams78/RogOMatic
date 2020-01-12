package gamedata

import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower
import gamedata.items.ScrollPower.ScrollPower

trait Fact

object Fact {

  case class MagicItemKnowledge[A, P](attribute: A, power: P) extends Fact

  type PotionKnowledge = MagicItemKnowledge[Colour, PotionPower]

  def PotionKnowledge(colour: Colour, power: PotionPower): PotionKnowledge = MagicItemKnowledge(colour, power)

  implicit class IsPotionKnowledge(self: PotionKnowledge) {
    def colour: Colour = self.attribute
  }

  type ScrollKnowledge = MagicItemKnowledge[String, ScrollPower]

  def ScrollKnowledge(title: String, power: ScrollPower): ScrollKnowledge = MagicItemKnowledge(title, power)

  implicit class IsScrollKnowledge(self: ScrollKnowledge) {
    def title: String = self.attribute
  }

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



