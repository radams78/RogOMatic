package gamedata.fact

/** A typeclass for objects from which facts can be deduced */
trait ProvidesKnowledge[T] {
  /** The set of facts that can be deduced from this object */
  def implications(self: T): Set[Fact]
}

object ProvidesKnowledge {

  implicit final class ProvidesKnowledgeOps[T](self: T)(implicit s: ProvidesKnowledge[T]) {
    def implications: Set[Fact] = s.implications(self)
  }

  implicit def optionProvidesKnowledge[T](implicit s: ProvidesKnowledge[T]): ProvidesKnowledge[Option[T]] = {
    case None => Set()
    case Some(t) => t.implications
  }

  implicit def factProvidesKnowledge: ProvidesKnowledge[Fact] = (self: Fact) => Set(self)

  implicit def setProvidesKnowledge[T](implicit s: ProvidesKnowledge[T]): ProvidesKnowledge[Set[T]] = (self: Set[T]) => self.flatMap(s.implications)
}



