package gamedata

import gamedata.Domain._
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









