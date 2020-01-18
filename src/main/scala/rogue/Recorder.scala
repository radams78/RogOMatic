package rogue

import domain.Domain._
import domain.pLift
import expert.pGameState
import gamedata.{Fact, pInventory}
import gamestate.IRecorder
import rogue.Event.Event


class Recorder extends IRecorder {
  def getScreen: pLift[String] = _gameState.screen

  def knowledge: Set[Fact] = _gameState.knowledge

  private var _gameOver: Boolean = false
  private var _gameState: pGameState = pGameState()
  private var score: Int = 0

  def gameState: pGameState = _gameState

  def getInventory: pInventory = _gameState.inventory

  def recordScreen(_screen: String): Unit = _gameState = _gameState.copy(screen = pLift.Known(_screen))

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

  def recordInventory(_inventory: pInventory): Unit = _gameState = _gameState.copy(inventory = _inventory)

  def recordCommand(command: Command): Either[String, Unit] = for (gs <- _gameState.nextTurn.merge(pGameState(command)))
    yield {
      _gameState = gs
    }
}
