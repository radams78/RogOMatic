package gamedata

import domain.Domain

trait pOption[+D] {
  def foreach[U](f: D => U): Unit

  def map[E](f: D => E): pOption[E]
}

object pOption {

  def known[D](x: Option[D]): pOption[D] = x match {
    case scala.Some(d) => Some(d)
    case None => NONE
  }

  case class Some[+D](d: D) extends pOption[D] {
    override def map[E](f: D => E): pOption[E] = Some(f(d))

    override def foreach[U](f: D => U): Unit = f(d)
  }

  case object UNKNOWN extends pOption[Nothing] {
    override def map[E](f: Nothing => E): pOption[E] = UNKNOWN

    override def foreach[U](f: Nothing => U): Unit = ()
  }

  implicit def domain[D](implicit s: Domain[D]): Domain[pOption[D]] = (x: pOption[D], y: pOption[D]) => (x, y) match {
    case (UNKNOWN, y) => Right(y)
    case (x, UNKNOWN) => Right(x)
    case (NONE, NONE) => Right(NONE)
    case (Some(x), Some(y)) => s.merge(x, y).map(Some(_))
    case _ => Left(s"Incompatible information: $x and $y")
  }

  case object NONE extends pOption[Nothing] {
    override def map[E](f: Nothing => E): pOption[E] = NONE

    override def foreach[U](f: Nothing => U): Unit = ()
  }

}
