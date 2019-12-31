package domain

/** Invariants:
 * - x.merge(x) == x */
trait Domain[D] {
  def merge(x: D, y: D): Either[String, D]
}

object Domain {
  implicit def optionDomain[D](implicit s: Domain[D]): Domain[Option[D]] = (x: Option[D], y: Option[D]) => (x, y) match {
    case (None, y) => Right(y)
    case (x, None) => Right(x)
    case (Some(a), Some(b)) => for {c <- a.merge(b)} yield Some(c)
  }

  implicit final class DomainOps[D](self: D)(implicit s: Domain[D]) {
    def merge(that: D): Either[String, D] = s.merge(self, that)
  }

  def flatDomain[T]: Domain[T] = (x: T, y: T) => if (x == y) Right(x) else Left(s"Incompatible information: $x and $y")

  implicit def intDomain: Domain[Int] = flatDomain

  implicit def stringDomain: Domain[String] = flatDomain

  implicit def mapDomain[K, V](implicit s: Domain[V]): Domain[Map[K, V]] = (x: Map[K, V], y: Map[K, V]) => y.foldLeft[Either[String, Map[K, V]]](Right(x))({ case (maybeMap: Either[String, Map[K, V]], (k: K, v: V)) =>
    for {
      map <- maybeMap
      vvv <- map.get(k) match {
        case Some(vv) => v.merge(vv)
        case None => Right(v)
      }
    } yield map.updated(k, vvv)
  })
}