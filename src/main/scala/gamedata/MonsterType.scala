package gamedata

import domain.Domain

object MonsterType extends ParsableEnum {

  type MonsterType = Value
  override val name: String = "monster type"
  val AQUATOR: MonsterType = Val("aquator")
  val BAT: MonsterType = Val("bat")
  val CENTAUR: MonsterType = Val("centaur")
  val DRAGON: MonsterType = Val("dragon")
  val EMU: MonsterType = Val("emu")
  val VENUS_FLY_TRAP: MonsterType = Val("venus fly-trap")
  val HOBGOBLIN: MonsterType = Val("hobgoblin")
  val GRIFFIN: MonsterType = Val("griffin")
  val ICE_MONSTER: MonsterType = Val("ice monster")
  val JABBERWOCK: MonsterType = Val("jabberwock")
  val KESTREL: MonsterType = Val("kestrel")
  val LEPRECHAUN: MonsterType = Val("leprechaun")
  val MEDUSA: MonsterType = Val("medusa")
  val NYMPH: MonsterType = Val("nymph")
  val ORC: MonsterType = Val("orc")
  val PHANTOM: MonsterType = Val("phantom")
  val QUAGGA: MonsterType = Val("quagga")
  val RATTLESNAKE: MonsterType = Val("rattlesnake")
  val SNAKE: MonsterType = Val("snake")
  val TROLL: MonsterType = Val("troll")
  val UNICORN: MonsterType = Val("black unicorn")
  val VAMPIRE: MonsterType = Val("vampire")
  val WRAITH: MonsterType = Val("wraith")
  val XEROC: MonsterType = Val("xeroc")
  val YETI: MonsterType = Val("yeti")

  protected case class Val(name: String) extends super.Val

  implicit def toVal(x: Value): Val = x.asInstanceOf[Val]

  implicit def domain: Domain[MonsterType] = Domain.flatDomain

  val ZOMBIE: MonsterType = Val("zombie")
}
