package model.rogue

trait IActuator {
  def displayInventoryScreen(): Unit

  def clearInventoryScreen(): Unit

  def startGame()

}
