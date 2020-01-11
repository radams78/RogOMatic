package expert

import domain.Domain
import domain.Domain._
import gamedata.Fact.ScrollKnowledge
import gamedata.ProvidesKnowledge._
import gamedata.UsesKnowledge._
import gamedata.{Fact, pInventory, pOption}
import rogue.Command
import rogue.Command._

case class pGameState(screen: Option[String], inventory: pInventory, knowledge: Set[Fact], lastCommand: pOption[Command]) {
  def scrollKnowledge: Set[ScrollKnowledge] = knowledge.collect({
    case sk: ScrollKnowledge => sk
  })

  def complete: Either[String, pGameState] = for {
    inferredInventory <- knowledge.foldLeft[Either[String, pInventory]](Right(inventory))({
      case (Left(err), _) => Left(err)
      case (Right(inv), fact) => inv.infer(fact)
    })
    inferredInventory2 <- lastCommand match {
      case pOption.UNKNOWN => Right(inferredInventory)
      case pOption.NONE => Right(inferredInventory)
      case pOption.Some(command) => inferredInventory.infer(command)
    }
    inferredCommand <- lastCommand match {
      case pOption.UNKNOWN => Right(pOption.UNKNOWN)
      case pOption.NONE => Right(pOption.NONE)
      case pOption.Some(command) => for {
        command2 <- command.infer(inventory)
        command3 <- knowledge.foldLeft[Either[String, Command]](Right(command2))({
          case (Left(err), _) => Left(err)
          case (Right(cmd), fact) => cmd.infer(fact)
        })
      } yield pOption.Some(command3)
    }
  } yield pGameState(
    screen,
    inferredInventory2,
    knowledge.union(inferredInventory2.implications).union(inferredCommand.asInstanceOf[pOption[Command]].implications),
    inferredCommand
  )

  // TODO Infer stuff from screen?
}

object pGameState {
  def apply(): pGameState = new pGameState(None, pInventory(), Set(), pOption.UNKNOWN)

  def apply(lastCommand: Command): pGameState = new pGameState(None, pInventory(), Set(), pOption.Some(lastCommand))

  implicit def domain: Domain[pGameState] = (x: pGameState, y: pGameState) => for {
    screen <- x.screen.merge(y.screen)
    inventory <- x.inventory.merge(y.inventory)
    lastCommand <- x.lastCommand.merge(y.lastCommand)
    gs <- pGameState(screen, inventory, x.knowledge.union(y.knowledge), lastCommand).complete
  } yield gs
}