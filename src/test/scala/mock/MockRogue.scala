package mock

import rogue.IRogue

/** A mock Rogue process */
object MockRogue extends IRogue {
  /** True if the method [[start()]] has been called */
  var isStarted: Boolean = false

  override def start(): Unit = isStarted = true
}
