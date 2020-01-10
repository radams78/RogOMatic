package rogue

import domain.Domain._
import expert.pGameState
import gamedata.{pInventory, pOption}
import gamestate._
import rogue.Event.Event

trait IRecorder {
  def getInventory: pInventory

  def getScreen: String

  def getScore: Int

  def gameOver: Boolean
}

class Recorder extends IRecorder {
  private var _gameOver: Boolean = false
  private var _gameState: pGameState = pGameState()
  private var inventory: pInventory = _
  private var score: Int = 0
  private var screen: String = ""

  def gameState: pGameState = _gameState

  def getScrollKnowledge: ScrollKnowledge = gameState.scrollKnowledge

  def getInventory: pInventory = inventory

  def recordScreen(_screen: String): Unit = screen = _screen

  def getScreen: String = screen

  def getScore: Int = score

  def gameOver: Boolean = _gameOver

  def recordEvent(e: Event): Either[String, Unit] =
    for (gs <- _gameState.merge(e.inference)) yield {
      _gameState = gs
    }

  def recordFinalScore(_score: Int): Unit = {
    _gameOver = true
    score = _score
  }

  def recordInventory(_inventory: pInventory): Unit = inventory = _inventory

  def recordCommand(command: Command): Either[String, Unit] = for (gs <- pGameState(None, pInventory(), _gameState.scrollKnowledge, pOption.Some(command)).complete)
    yield {
      _gameState = gs
    }
}
