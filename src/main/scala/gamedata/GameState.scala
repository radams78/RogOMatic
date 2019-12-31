package gamedata

import gamedata.Colour.Colour
import gamedata.Domain._
import gamedata.PotionPower.PotionPower
import gamedata.ScrollPower.ScrollPower
import rogue.Command

/** Partial information about the state of the game 
 *
 * Invariants: if lastCommand == Some(lc) then
 * - scrollKnowledge.merge(lc.scrollKnowledge) == Right(scrollKnowledge)
 * - potionKnowledge.merge(lc.potionKnowledge) == Right(potionKnowledge)
 * - lc.infer(scrollKnowledge) == Right(lc)
 * - lc.infer(potionKnowledge) == Right(lc) */
class GameState(val scrollKnowledge: ScrollKnowledge = ScrollKnowledge(),
                val potionKnowledge: PotionKnowledge = PotionKnowledge(),
                val lastCommand: Option[Command] = None) {
  def merge(that: GameState): Either[String, GameState] = for {
    inferredScrollKnowledge <- scrollKnowledge.merge(that.scrollKnowledge)
    inferredPotionKnowledge <- potionKnowledge.merge(that.potionKnowledge)
    inferredLastCommand <- lastCommand.merge(that.lastCommand)
    gs <- GameState.build(inferredScrollKnowledge, inferredPotionKnowledge, inferredLastCommand)
  } yield gs
}

object GameState {
  def apply(): GameState = new GameState()

  def apply(lastCommand: Command): GameState =
    new GameState(lastCommand.scrollKnowledge, lastCommand.potionKnowledge, Some(lastCommand))

  def build(scrollKnowledge: ScrollKnowledge, potionKnowledge: PotionKnowledge, lastCommand: Option[Command]): Either[String, GameState] =
    lastCommand.fold[Either[String, GameState]](
      Right(new GameState(scrollKnowledge, potionKnowledge, None))
    )((cmd: Command) => for {
      sp <- scrollKnowledge.merge(cmd.scrollKnowledge)
      pp <- potionKnowledge.merge(cmd.potionKnowledge)
      command <- cmd.infer(scrollKnowledge)
      command2 <- command.infer(potionKnowledge)
    } yield new GameState(sp, pp, Some(command2)))

  implicit def domain: Domain[GameState] = (x: GameState, y: GameState) => x.merge(y)
}

case class ScrollKnowledge(private val powers: Map[String, ScrollPower]) {
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  def getPower(title: String): Option[ScrollPower] = powers.get(title)
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}

case class PotionKnowledge(private val powers: Map[Colour, PotionPower]) {
  def getColour(p: PotionPower): Option[Colour] = powers.find(_._2 == p).map(_._1)

  def getPower(c: Colour): Option[PotionPower] = powers.get(c)
}

object PotionKnowledge {
  def apply(): PotionKnowledge = new PotionKnowledge(Map())

  implicit def domain: Domain[PotionKnowledge] = (x: PotionKnowledge, y: PotionKnowledge) => x.powers.merge(y.powers).map(new PotionKnowledge(_))
}

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