package gamedata

/** An [[Enumeration]] with a parse method, which behaves similarly to [[Enumeration.withName]] but
 * uses [[Either]] for error handling instead of throwing an exception */
trait ParsableEnum extends Enumeration {
  /** Name of the set of values */
  val name: String

  /** Returns the value with the given name, or an error message if there is no value with that name */
  def parse(description: String): Either[String, Value] = try {
    Right(withName(description))
  } catch {
    case _: NoSuchElementException => Left(s"Unrecognised $name: $description")
  }
}
