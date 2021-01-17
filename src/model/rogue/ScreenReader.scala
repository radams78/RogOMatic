package model.rogue

/** Take the screen displayed by Rogue and make it available via a getter and the observer pattern.
 * 
 * This is an [[IScreenObserver]] that can pass a screen on to other [[IScreenObserver]]s and make it available via
 * the getScreen command. Until it receives */
class ScreenReader private () extends IScreenReader {
  private[this] var _screen : Option[Screen] = None
  
  /** Returns the current screen displayed by Rogue. Returns None if the Rogue process has not yet started or has ended. */
  def readScreen(): Option[Screen] = _screen

  def notify(screen : Screen): Unit = {
    _screen = Some(screen)
    for (observer <- screenObservers) observer.notify(screen)
  }
  
  private var screenObservers : Set[IScreenObserver] = Set()

  /** Add an observer that listens for the screen retrieved from Rogue */
  def addScreenObserver(observer: IScreenObserver): Unit = screenObservers = screenObservers + observer
}

object ScreenReader {
  def apply() : ScreenReader = new ScreenReader()
}