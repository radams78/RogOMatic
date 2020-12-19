package model

/** An object that can observe an [[IRoguePlayer]] */
trait IScreenObserver {
  /** Notify all observers that this is the screen displayed by Rogue */
  def notify(screen: Seq[String]): Unit
}
