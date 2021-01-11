package model.rogue

/** An object that can observe an [[IRogue]] */
trait IScreenObserver {
  /** Notify all observers that this is the screen displayed by model.rogue.Rogue */
  def notify(screen: Screen): Unit
}
