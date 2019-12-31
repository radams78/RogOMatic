package rogue

import gamedata.Event.Event
import gamedata.{Event, GameState, Inventory, ScrollKnowledge}

import scala.annotation.tailrec
import scala.util.matching.Regex

/** High-level communication with the game of Rogue. */
class RogueActuator(rogue: IRogue, recorder: Recorder) {
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

  /** Send a command to Rogue */
  def sendCommand(command: Command): Either[String, Unit] = {
    for {_ <- recorder.recordCommand(command)
         _ <- {
           for (k <- command.keypresses) rogue.sendKeypress(k)
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
        for {e <- Event.interpretMessage(message)
             _ <- recorder.recordEvent(e)
             } yield ()
        rogue.sendKeypress(' ')
        update()
      case message => for {
        e <- Event.interpretMessage(message)
        _ <- recorder.recordEvent(e)
      } yield ()
    }
  }
}

object RogueActuator {
  private val moreRegex: Regex = """(.*)-more-""".r.unanchored
  private val scoreRegex: Regex = """(\d+) gold""".r.unanchored
}

class Recorder {
  private var _gameOver: Boolean = false
  private var gameState: GameState = GameState()
  private var inventory: Inventory = _
  private var score: Int = 0
  private var screen: String = ""

  def getScrollKnowledge: ScrollKnowledge = gameState.scrollKnowledge

  def getInventory: Inventory = inventory

  def recordScreen(_screen: String): Unit = screen = _screen

  def getScreen: String = screen

  def getScore: Int = score

  def gameOver: Boolean = _gameOver

  def recordEvent(e: Event): Either[String, Unit] = for (gs <- gameState.merge(e.inference)) yield {
    gameState = gs
    ()
  }

  def recordFinalScore(_score: Int): Unit = {
    _gameOver = true
    score = _score
  }

  def recordInventory(_inventory: Inventory): Unit = inventory = _inventory

  def recordCommand(command: Command): Either[String, Unit] =
    for (gs <- GameState.build(gameState.scrollKnowledge, gameState.potionKnowledge, Some(command))) yield {
      gameState = gs
      ()
    }
}