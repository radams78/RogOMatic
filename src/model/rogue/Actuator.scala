package model.rogue

class Actuator(rogue : IRogue) extends IActuator {
  override def displayInventoryScreen(): Unit = rogue.sendKeypress('i')
  
  override def clearInventoryScreen(): Unit = rogue.sendKeypress(' ')

  override def startGame(): Unit = rogue.startGame()
}

object Actuator {
  def apply(rogue : IRogue) : IActuator = new Actuator(rogue)
}