package gamedata

import gamedata.fact.ProvidesKnowledge._
import gamedata.fact.{Fact, ProvidesKnowledge}

/** Trait for domains for which we can take a [[Fact]] and use it to infer more information about the object */
trait UsesKnowledge[T] {
  /** Returns either the object with the information from fact incorporated, or an error message if the information
   * in self and the information in fact are contradictory. */
  def infer(self: T, fact: Fact): Either[String, T]
}

object UsesKnowledge {

  implicit final class UsesKnowledgeOps[T](self: T)(implicit s: UsesKnowledge[T]) {
    /** Infer all the information from all the facts provided by the object providesKnowledge */
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