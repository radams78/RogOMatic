package integration

trait IModelObserver {
  def notify(screen: Seq[String]): Unit
}
