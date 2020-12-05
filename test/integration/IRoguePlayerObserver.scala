package integration

/** An object that can observe an [[IRoguePlayer]] */
trait IRoguePlayerObserver {
  def notify(screen: Seq[String]): Unit
}
