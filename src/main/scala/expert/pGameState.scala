package expert

import domain.Domain
import domain.Domain._
import gamedata.{pInventory, pOption}
import gamestate.ScrollKnowledge
import rogue.Command

case class pGameState(screen: Option[String], inventory: pInventory, scrollKnowledge: ScrollKnowledge, lastCommand: pOption[Command]) {
  def complete: Either[String, pGameState] = for {
    inferredInventory <- inventory.infer(scrollKnowledge)
    inferredInventory2 <- lastCommand match {
      case pOption.UNKNOWN => Right(inferredInventory)
      case pOption.NONE => Right(inferredInventory)
      case pOption.Some(command) => inferredInventory.infer(command)
    }
    inferredScrollKnowledge <- scrollKnowledge.infer(inventory)
    inferredScrollKnowledge2 <- lastCommand match {
      case pOption.UNKNOWN => Right(inferredScrollKnowledge)
      case pOption.NONE => Right(inferredScrollKnowledge)
      case pOption.Some(command) => inferredScrollKnowledge.infer(command)
    }
    inferredCommand <- lastCommand match {
      case pOption.UNKNOWN => Right(pOption.UNKNOWN)
      case pOption.NONE => Right(pOption.NONE)
      case pOption.Some(command) => for {
        command2 <- command.infer(inventory)
        command3 <- command2.infer(scrollKnowledge)
      } yield pOption.Some(command3)
    }
  } yield pGameState(screen, inferredInventory2, inferredScrollKnowledge2, inferredCommand) // TODO Infer stuff from screen?
}

object pGameState {
  def apply(): pGameState = pGameState(None, pInventory(), ScrollKnowledge(), pOption.UNKNOWN)

  def apply(lastCommand: Command): pGameState = pGameState(None, pInventory(), ScrollKnowledge(), pOption.Some(lastCommand))

  implicit def domain: Domain[pGameState] = (x: pGameState, y: pGameState) => for {
    screen <- x.screen.merge(y.screen)
    inventory <- x.inventory.merge(y.inventory)
    scrollKnowledge <- x.scrollKnowledge.merge(y.scrollKnowledge)
    lastCommand <- x.lastCommand.merge(y.lastCommand)
    gs <- pGameState(screen, inventory, scrollKnowledge, lastCommand).complete
  } yield gs
}