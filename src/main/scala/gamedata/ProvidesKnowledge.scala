package gamedata

import gamestate.{PotionKnowledge, ScrollKnowledge}

trait ProvidesKnowledge { // TODO Not compositional
  def scrollKnowledge: Either[String, ScrollKnowledge] = Right(ScrollKnowledge()) // TODO Demand Right?

  def potionKnowledge: Either[String, PotionKnowledge] = Right(PotionKnowledge()) // TODO Demand Right?
}

trait UsesKnowledge[T] {
  def infer(_this: T, scrollKnowledge: ScrollKnowledge): Either[String, T]

  def infer(_this: T, potionKnowledge: PotionKnowledge): Either[String, T]
}

object UsesKnowledge {

  implicit final class UsesKnowledgeOps[T](self: T)(implicit s: UsesKnowledge[T]) {
    def infer(item: ProvidesKnowledge): Either[String, T] = for {
      sk <- item.scrollKnowledge
      item2 <- s.infer(self, sk)
      pk <- item.potionKnowledge
      item3 <- s.infer(item2, pk)
    } yield item3
  }

}