package model

import model.rogue.Screen

class ScreenReader {
  private var _screen : Option[Screen] = None
  
  def readScreen(): Option[Screen] = _screen

  def notify(screen : Screen): Unit = {
    _screen = Some(screen)
    for (observer <- screenObservers) observer.notify(screen)
  }
  
  private var screenObservers : Set[IScreenObserver] = Set()

  /** Add an observer that listens for the screen retrieved from Rogue */
  def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer
}
