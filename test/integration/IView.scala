package integration

trait IView extends IRoguePlayerObserver {
  def notify(screen: Seq[String]): Unit
}
