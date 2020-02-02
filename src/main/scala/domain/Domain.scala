package domain

/** A typeclass for objects that give partial information about an entity in the game of Rogue.
 *
 * Given a domain D, we can merge two objects x,y: D to form a new element z = merge(x,y) : D. The intention is that 
 * z contains the union of the information in x and the information in y.
 *
 * This method should return an error message if x and y contain contradictory information, i.e. it is impossible
 * for them both to be partial approximations of the same object. 
 *
 * A domain should satisfy the following, for all x,y,z:D :-
 * - merge(x, x) == x
 * - merge(x, y) == merge(y, x)
 * - merge(x, y).flatMap(merge(_, z)) == merge(y, z).flatMap(merge(x, _)) */
trait Domain[D] {
  def merge(x: D, y: D): Either[String, D]
}

object Domain {
  implicit final class DomainOps[D](self: D)(implicit s: Domain[D]) {
    def merge(that: D): Either[String, D] = s.merge(self, that)
  }

  def flatDomain[T]: Domain[T] = (x: T, y: T) => if (x == y) Right(x) else Left(s"Incompatible information: $x and $y")

  implicit def intDomain: Domain[Int] = flatDomain

  implicit def stringDomain: Domain[String] = flatDomain

  implicit def mapDomain[K, V](implicit s: Domain[V]): Domain[Map[K, V]] = (x: Map[K, V], y: Map[K, V]) => y.foldLeft[Either[String, Map[K, V]]](Right(x))({ case (maybeMap: Either[String, Map[K, V]], (k, v)) =>
    for {
      map <- maybeMap
      vvv <- map.get(k) match {
        case Some(vv) => v.merge(vv)
        case None => Right(v)
      }
    } yield map.updated(k, vvv)
  })
}



