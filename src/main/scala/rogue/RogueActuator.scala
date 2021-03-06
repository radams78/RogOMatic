package rogue

import gamedata.{Event, Report}
import gamestate.Inventory

import scala.annotation.tailrec
import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
class RogueActuator(rogue: IRogue) extends IRogueActuator {
  override def start(): Either[String, Report.GameOn] = {
    rogue.start()
    for {
      report <- getReport(Set())
    } yield report match {
      case r: Report.GameOn => r
      case _: Report.GameOver => return Left("Game over before first move")
    }
  }

  override def sendCommand(command: Command): Either[String, Report] = {
    for (k <- command.keypresses) rogue.sendKeypress(k)
    getReport(Set())
  }

  @tailrec
  private def getReport(acc: Set[Event]): Either[String, Report] = {
    val screen: String = rogue.getScreen
    val lines: Array[String] = screen.split("\n").map(_.padTo(80, ' '))
    if (!lines.last.exists(_ != ' ')) return {
      for (score <- readGameOverScreen(screen))
        yield Report.GameOver(screen, score)
    }
    lines.head match {
      case RogueActuator.moreRegex(message) =>
        Event.interpretMessage(message) match {
          case Left(err) => Left(s"Error when parsing message line $message: $err")
          case Right(events) =>
            rogue.sendKeypress(' ')
            getReport(acc ++ events)
        }
      case message =>
        for {
          events <- Event.interpretMessage(message)
          inventory <- {
            rogue.sendKeypress('i')
            val screen: String = rogue.getScreen
            rogue.sendKeypress(' ')
            Inventory.parseInventoryScreen(screen)
          }
          report <- Report.GameOn.build(screen, inventory, acc ++ events)
        } yield report
    }
  }

  private def readGameOverScreen(screen: String): Either[String, Int] = {
    screen match {
      case RogueActuator.scoreRegex(score) => try {
        Right(score.toInt)
      } catch {
        case e: NumberFormatException => Left(s"Could nat parse screen: $screen")
      }
      case _ => Left(s"Could not parse screen: $screen")
    }
  }
}

object RogueActuator {
  private val moreRegex: Regex = """(.*)-more-""".r.unanchored
  private val scoreRegex: Regex = """(\d+) gold""".r.unanchored
}