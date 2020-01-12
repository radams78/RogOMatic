package gamedata.items

import domain.Domain._
import gamedata.items.Gem.Gem

/** A ring */
case class Ring(gem: Gem) extends Item {
  override def merge(that: Item): Either[String, Item] = that match {
    case Ring(thatGem) => for {inferredGem <- gem.merge(thatGem)} yield Ring(inferredGem)
    case _ => Left(s"Incompatible items: $this and $that")
  }
}
