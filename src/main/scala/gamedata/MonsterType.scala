package gamedata

object MonsterType extends Enumeration {

  type MonsterType = Value
  val HOBGOBLIN: MonsterType = Val("hobgoblin")

  def parse(description: String): Either[String, MonsterType] =
    values
      .find(_.name == description)
      .map(Right(_))
      .getOrElse(Left(s"Unrecognised monster type: $description"))

  protected case class Val(name: String) extends super.Val

  implicit def toVal(x: Value): Val = x.asInstanceOf[Val]

  implicit def domain: Domain[MonsterType] = Domain.flatDomain
}
