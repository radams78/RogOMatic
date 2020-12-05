package integration

import rogue.SensorObserver

trait IView extends IModelObserver {
  def notify(screen: Seq[String]): Unit
}
