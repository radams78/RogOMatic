import rogue.{IScreenObserver, Screen}

class ScreenView extends IScreenObserver {
  override def notify(screen: Screen): Unit = println(screen)
}
