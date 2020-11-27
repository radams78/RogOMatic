package main

import rogue.IRogue
import view.IView

class RogOMatic(rogue : IRogue, view : IView) {
  val sensor: Sensor = new Sensor(rogue)
  sensor.subscribe(view)

  def startGame(): Unit = sensor.broadcastAll()
}
