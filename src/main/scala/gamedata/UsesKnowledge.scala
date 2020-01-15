package gamedata

import gamedata.ProvidesKnowledge._

trait UsesKnowledge[T] {
  def infer(self: T, fact: Fact): Either[String, T]
}

object UsesKnowledge {

  implicit final class UsesKnowledgeOps[T](self: T)(implicit s: UsesKnowledge[T]) {
    def infer[S: ProvidesKnowledge](providesKnowledge: S): Either[String, T] =
      providesKnowledge.implications.foldLeft[Either[String, T]](
        Right(self)
      )({
        case (Left(err), _) => Left(err)
        case (Right(t), fact) => s.infer(t, fact)
      })
  }

  implicit def optionUsesKnowledge[T](implicit s: UsesKnowledge[T]): UsesKnowledge[Option[T]] = (self: Option[T], fact: Fact) => self match {
    case None => Right(None)
    case Some(t) => for (newT <- t.infer(fact)) yield Some(newT)
  }
}