package rogue

import gamedata.Inventory
import gamestate.{GameState, ScrollKnowledge}
import rogue.Event.Event

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
