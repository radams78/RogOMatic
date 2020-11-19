package model

trait RoguePlayerObserver {

  def updateScreen(screen: Screen): Unit

  def updateGameOver(): Unit
}
