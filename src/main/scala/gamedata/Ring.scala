package gamedata

import domain.Domain._
import gamedata.Gem.Gem

/** A ring */
case class Ring(gem: Gem) extends Item {
  override def merge[T <: Item](that: T): Either[String, T] = that match {
    case Ring(thatGem) => for {inferredGem <- gem.merge(thatGem)} yield Ring(inferredGem).asInstanceOf[T]
    case _ => Left(s"Incompatible items: $this and $that")
  }
}
