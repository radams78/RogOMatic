package expert

import domain.Domain._
import domain.{Domain, pLift}
import gamedata.ProvidesKnowledge._
import gamedata.UsesKnowledge._
import gamedata.{Fact, ProvidesKnowledge, pCommand, pInventory}

case class pGameState(screen: pLift[String], inventory: pInventory, knowledge: Set[Fact], lastCommand: pLift[Option[pCommand]]) {
  def nextTurn: pGameState = pGameState(pLift.UNKNOWN, pInventory(), knowledge, pLift.UNKNOWN)

  def complete: Either[String, pGameState] = for {
    inferredInventory <- knowledge.foldLeft[Either[String, pInventory]](Right(inventory))({
      case (Left(err), _) => Left(err)
      case (Right(inv), fact) => inv.infer(fact)
    })
    inferredInventory2 <- lastCommand match {
      case pLift.UNKNOWN => Right(inferredInventory)
      case pLift.Known(None) => Right(inferredInventory)
      case pLift.Known(Some(command)) => inferredInventory.infer(command)
    }
    inferredCommand <- lastCommand match {
      case pLift.UNKNOWN => Right(pLift.UNKNOWN)
      case pLift.Known(None) => Right(pLift.Known(None))
      case pLift.Known(Some(command)) => for {
        command2 <- command.infer(inventory)
        command3 <- knowledge.foldLeft[Either[String, pCommand]](Right(command2))({
          case (Left(err), _) => Left(err)
          case (Right(cmd), fact) => cmd.infer(fact)
        })
      } yield pLift.Known(Some(command3))
    }
  } yield {
    val commandImplications: Set[Fact] = inferredCommand match {
      case pLift.UNKNOWN => Set()
      case pLift.Known(None) => Set()
      case pLift.Known(Some(command)) => command.implications
    }
    pGameState(
      screen,
      inferredInventory2,
      knowledge.union(inferredInventory2.implications).union(commandImplications),
      inferredCommand
    )
  }

  // TODO Infer stuff from screen?
}

object pGameState {
  def apply(): pGameState = new pGameState(pLift.UNKNOWN, pInventory(), Set(), pLift.UNKNOWN)

  def apply(lastCommand: pCommand): pGameState = new pGameState(pLift.UNKNOWN, pInventory(), Set(), pLift.Known(Some(lastCommand)))

  implicit def domain: Domain[pGameState] = (x: pGameState, y: pGameState) => for {
    screen <- x.screen.merge(y.screen)
    inventory <- x.inventory.merge(y.inventory)
    lastCommand <- x.lastCommand.merge(y.lastCommand)
    gs <- pGameState(screen, inventory, x.knowledge.union(y.knowledge), lastCommand).complete
  } yield gs

  implicit def providesKnowledge: ProvidesKnowledge[pGameState] = {
    case pGameState(screen, inventory, knowledge, lastCommand) =>
      inventory.implications union
        knowledge union
        (lastCommand match {
          case pLift.UNKNOWN => Set()
          case pLift.Known(None) => Set()
          case pLift.Known(Some(command)) => command.implications
        })
  }
}