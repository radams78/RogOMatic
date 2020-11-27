package view

import main.SensorObserver

trait IView extends SensorObserver {
  def notify(screen: Seq[String])
}
