package model.rogue

trait IScreenReader extends IScreenObserver {
  def readScreen(): Option[Screen]

}
