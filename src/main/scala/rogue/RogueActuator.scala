package rogue


import gamedata.Inventory

import scala.annotation.tailrec
import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
class RogueActuator(rogue: IRogue, recorder: Recorder) {
  /** Start the game of Rogue and read the first screen and inventory */
  def start(): Either[String, Unit] = {
    rogue.start()
    for {
      _ <- update()
      inventory <- {
        rogue.sendKeypress('i')
        val screen: String = rogue.getScreen
        rogue.sendKeypress(' ')
        Inventory.parseInventoryScreen(screen)
      }
    } yield recorder.recordInventory(inventory)
  }

  /** Send a command to Rogue, then read all possible information from the screen and inventory */
  def sendCommand(command: Command): Either[String, Unit] = {
    for {_ <- recorder.recordCommand(command)
         keys <- command.keypresses
         _ <- {
           for (k <- keys) rogue.sendKeypress(k)
           update()
         }
         _ <- if (!recorder.gameOver) {
           for (inventory <- {
             rogue.sendKeypress('i')
             val screen: String = rogue.getScreen
             rogue.sendKeypress(' ')
             Inventory.parseInventoryScreen(screen)
           }) yield {
             recorder.recordInventory(inventory)
             ()
           }
         } else Right(())
         } yield ()
  }


  @tailrec
  private def update(): Either[String, Unit] = {
    val screen: String = rogue.getScreen
    val lines: Array[String] = screen.split("\n").map(_.padTo(80, ' '))
    recorder.recordScreen(screen)
    if (!lines.last.exists(_ != ' ')) {
      screen match {
        case RogueActuator.scoreRegex(score) =>
          recorder.recordFinalScore(score.toInt)
          return Right(())
        case _ =>
          return Left(s"Could not parse screen: $screen")
      }
    }
    lines(0) match {
      case RogueActuator.moreRegex(message) =>
        readEvent(message)
        rogue.sendKeypress(' ')
        update()
      case message =>
        readEvent(message)
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