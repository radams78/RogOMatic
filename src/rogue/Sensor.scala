package rogue

class Sensor(rogue: IRogue) extends ISensor {
  def subscribe(observer: SensorObserver): Unit = ()

}
