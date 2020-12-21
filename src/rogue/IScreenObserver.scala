package rogue

/** An object that can observe an [[IRogue]] */
trait IScreenObserver {
  /** Notify all observers that this is the screen displayed by Rogue */
  def notify(screen: Seq[String]): Unit
}
