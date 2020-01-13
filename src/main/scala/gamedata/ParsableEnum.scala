package gamedata

import domain.Domain

/** An [[Enumeration]] with a parse method, which behaves similarly to [[Enumeration.withName]] but
 * uses [[Either]] for error handling instead of throwing an exception */
trait ParsableEnum extends Enumeration {
  /** Name of the set of values */
  val name: String

  /** Returns the value with the given name, or an error message if there is no value with that name */
  def parse(description: String): Either[String, Value] =
    values.find(_.name == description) match {
      case Some(value) => Right(value)
      case None => Left(s"Unrecognised $name: $description")
    }

  implicit def domain: Domain[Value] = Domain.flatDomain

  protected class Val(val name: String) extends super.Val

  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]

}
