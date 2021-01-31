package model.rogue

/** An observer that is notified whenever the screen displayed by Rogue changes */
trait IScreenReader extends IScreenObserver {
  def readScreen(): Option[Screen]
}
