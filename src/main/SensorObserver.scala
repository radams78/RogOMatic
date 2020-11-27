package main

trait SensorObserver {
  def notify(screen: Seq[String]) : Unit
}
