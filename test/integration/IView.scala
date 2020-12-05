package integration

import rogue.SensorObserver

trait IView extends IRoguePlayerObserver {
  def notify(screen: Seq[String]): Unit
}
