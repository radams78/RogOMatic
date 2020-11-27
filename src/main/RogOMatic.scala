package main

import rogue.IRogue
import view.IView

/** The main class.
 * 
 * When the program starts, this connects a game of Rogue to a view. */
class RogOMatic(rogue : IRogue, view : IView) {
  private val sensor: Sensor = new Sensor(rogue)
  sensor.subscribe(view)

  def startGame(): Unit = sensor.broadcastAll()
}
