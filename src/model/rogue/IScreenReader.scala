package model.rogue

trait IScreenReader {
  def readScreen(): Option[Screen]

}
