package model.rogue

/** Take the screen displayed by Rogue and make it available via a getter and the observer pattern.
 * 
 * An object of this class is injected when an [[IRogue]] object is created. After the game of Rogue starts,
 * readScreen() returns the current screen displayed by the Rogue process, and all observers are notified whenever
 * the screen changes. Before the game starts or after the game ends, readScreen will return None. */
// TODO Return None after game ends
class ScreenReader private () {
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