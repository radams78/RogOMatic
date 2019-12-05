package mock

import rogue.IRogue

/** Mock Rogue process */
class MockRogue(firstScreen: String, firstInventoryScreen: String) extends IRogue {
  private var state: State = INITIAL

  def isStarted: Boolean = state != INITIAL

  override def start(): Unit = state match {
    case INITIAL => state = SCREEN
    case _ => throw new Error("start() called after game started")
  }

  override def getScreen: String = state match {
    case INITIAL => throw new Error("getScreen called before game started")
    case SCREEN => firstScreen
    case INVENTORY => firstInventoryScreen
  }

  override def sendKeypress(keyPress: Char): Unit = (state, keyPress) match {
    case (SCREEN, 'i') => state = INVENTORY
    case (INVENTORY, ' ') => state = SCREEN
    case _ => throw new Error(s"Unexpected keypress: $keyPress in state $state")
  }

  private trait State

  private case object INITIAL extends State

  private case object SCREEN extends State

  private case object INVENTORY extends State

}
