package domain

import gamedata.fact.{Fact, UsesKnowledge}

sealed trait pLift[+T]

object pLift {

  case object UNKNOWN extends pLift[Nothing]

  case class Known[T](t: T) extends pLift[T]

  implicit def domain[T](implicit s: Domain[T]): Domain[pLift[T]] = (x: pLift[T], y: pLift[T]) => (x, y) match {
    case (UNKNOWN, y) => Right(y)
    case (x, UNKNOWN) => Right(x)
    case (Known(x), Known(y)) => for (z <- s.merge(x, y)) yield Known(z)
  }

  implicit def usesKnowledge[T](implicit s: UsesKnowledge[T]): UsesKnowledge[pLift[T]] = (self: pLift[T], fact: Fact) => self match {
    case pLift.UNKNOWN => Right(pLift.UNKNOWN)
    case pLift.Known(t) => for (newT <- s.infer(t, fact)) yield pLift.Known(newT)
  }
}
