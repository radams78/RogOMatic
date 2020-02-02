package domain

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