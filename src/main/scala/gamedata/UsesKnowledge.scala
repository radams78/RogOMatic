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

  implicit def pOptionUsesKnowledge[T](implicit s: UsesKnowledge[T]): UsesKnowledge[pOption[T]] = (self: pOption[T], fact: Fact) => self match {
    case pOption.UNKNOWN => Right(pOption.UNKNOWN)
    case pOption.NONE => Right(pOption.NONE)
    case pOption.Some(t) => for (tt <- t.infer(fact)) yield pOption.Some(tt)
  }
}