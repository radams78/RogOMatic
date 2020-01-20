package rogue

import gamedata.pInventory
import gamestate.IInputRecorder

import scala.annotation.tailrec
import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
class RogueActuator(rogue: IRogue, recorder: IInputRecorder) extends IRogueActuator {
  override def start(): Either[String, Unit] = {
    rogue.start()
    for {
      gameOver <- update()
    } yield if (gameOver) return Left("Error: game ended before first move") else ()
  }

  override def sendCommand(command: Command): Either[String, Unit] = {
    for {_ <- recorder.recordCommand(command)
         keys <- command.keypresses
         gameOver <- {
           for (k <- keys) rogue.sendKeypress(k)
           update()
         }
         } yield ()
  }

  @tailrec
  private def update(): Either[String, Boolean] = {
    val screen: String = rogue.getScreen
    val lines: Array[String] = screen.split("\n").map(_.padTo(80, ' '))
    recorder.recordScreen(screen)
    if (!lines.last.exists(_ != ' ')) {
      for (_ <- readGameOverScreen(screen))
        yield true
    }
    lines.head match {
      case RogueActuator.moreRegex(message) =>
        readEvent(message)
        rogue.sendKeypress(' ')
        update()
      case message =>
        readEvent(message).map((_: Unit) => false)
        rogue.sendKeypress('i')
        val screen: String = rogue.getScreen
        rogue.sendKeypress(' ')
        for {
          inventory <- pInventory.parseInventoryScreen(screen)
          _ <- recorder.recordInventory(inventory)
        } yield false
    }
  }

  private def readGameOverScreen(screen: String): Either[String, Unit] = {
    screen match {
      case RogueActuator.scoreRegex(score) =>
        recorder.recordFinalScore(score.toInt)
        Right(())
      case _ => Left(s"Could not parse screen: $screen")
    }
  }

  private def readEvent(message: String): Either[String, Unit] = {
    for {e <- Event.interpretMessage(message)
         _ <- recorder.recordEvent(e)
         } yield ()
  }
}

object RogueActuator {
  private val moreRegex: Regex = """(.*)-more-""".r.unanchored
  private val scoreRegex: Regex = """(\d+) gold""".r.unanchored
}