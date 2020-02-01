package domain

/** A typeclass for objects that give partial information about an entity in the game of Rogue.
 *
 * Given a domain D, we can merge two objects x,y: D to form a new element z = merge(x,y) : D. The intention is that 
 * z contains the union of the information in x and the information in y.
 *
 * This method should return an error message if x and y contain contradictory information, i.e. it is impossible
 * for them both to be partial approximations of the same object. */
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

trait pOption[+D]

object pOption {
  def Known[D](x: Option[D]): pOption[D] = x match {
    case None => NONE
    case scala.Some(d) => Some(d)
  }

  case object UNKNOWN extends pOption[Nothing]

  case object NONE extends pOption[Nothing]

  case class Some[D](d: D) extends pOption[D]

  implicit def domain[D](implicit s: Domain[D]): Domain[pOption[D]] = (x: pOption[D], y: pOption[D]) => (x, y) match {
    case (UNKNOWN, y) => Right(y)
    case (x, UNKNOWN) => Right(x)
    case (NONE, NONE) => Right(NONE)
    case (Some(x), Some(y)) => for (z <- s.merge(x, y)) yield Some(z)
    case _ => Left(s"Incompatible information $x and $y")
  }
}